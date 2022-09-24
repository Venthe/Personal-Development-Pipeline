package eu.venthe.jenkins.pipelines

@groovy.lang.Grapes([@Grab('com.fasterxml.jackson.core:jackson-databind:2.13.3'),
        @Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3'),
        @Grab('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3')])
@com.cloudbees.groovy.cps.NonCPS

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

def mapper = new ObjectMapper(new YAMLFactory())

static Map parseYaml(input, mapper) {
    def parsedJson = mapper.readTree(input)
    return mapper.convertValue(parsedJson, Map.class)
}

static String provideName(name, uses) {
    if (!name && !uses) {
        return "Shell"
    }

    if (name && uses) {
        return "[$uses] $name"
    }

    return name ? "$name" : "$uses"
}

def run(it, additionalEnvironmentVariables) {
    def uses = it['uses']
    def run = it['run']
    if (uses) {
        def result = loadAction(uses, it, additionalEnvironmentVariables)
        if (result) {
            println result
        }
    }

    if (run) {
        sh run
    }
}

static def prepareEnvVariablesForWithEnv(Map additionalEnvironmentVariables) {
    return additionalEnvironmentVariables.collect { "${it.key}=${it.value}" }
}

def handles(events) {
    return events.collect { it.toLowerCase() }.contains(params.EVENT_NAME.toLowerCase())
}

static def getStepName(job) {
    def stepName = provideName(job['name'], job['uses'])
    def stepDescription = job['description']

    return stepDescription ? "$stepName - $stepDescription" : stepName
}

def loadAction(name, context, additionalEnvironmentVariables) {
    binding.variables["with"] = context["with"]
    binding.variables["uses"] = context["uses"]
    binding.variables["additionalEnvironmentVariables"] = additionalEnvironmentVariables
    binding.variables["stepName"] = getStepName(context)
    // return evaluate(new File("${env.JENKINS_HOME}/${name}.groovy"))
    return load "${env.JENKINS_HOME}/${name}.groovy"
}

properties([parameters([string(name: 'EVENT_NAME', defaultValue: 'push', description: ''),
                        string(name: 'REF', defaultValue: 'sampleRef', description: ''),
                        string(name: 'PROJECT_NAME', defaultValue: 'sampleProjectName', description: '')])])

def prepareWorkflow(workflowFile, mapper) {
    def additionalEnvironmentVariables = [:]
    def workflow = parseYaml(readFile(workflowFile.toString()), mapper)
    def action = {
        if (!handles(workflow['on'])) {
            print "Event not handled"
            return
        }
        workflow['jobs'].each { jobName, job ->
            docker.image(job['runs-on']).inside {
                stage("[${workflow['name']}/${jobName}]") {
                    echo job['steps'].toString();
                    job['steps'].each {
                        echo it.toString();
                        withEnv(prepareEnvVariablesForWithEnv(additionalEnvironmentVariables)) { ->
                            run(it, additionalEnvironmentVariables)
                        }
                    }
                }
            }
        }
    };
    return ["${workflow['name']}: ${workflowFile.name}":action];
}

node {
    def workflows;
    stage("Prepre workflows") {
        workflows = findFiles(glob: '.jenkins/workflows/*.y*ml').collectEntries { prepareWorkflow(it, mapper) }
        workflows.failFast = false;
    }
    parallel workflows
}
