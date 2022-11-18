package eu.venthe.jenkins.pipelines

@groovy.lang.Grapes([@Grab('com.fasterxml.jackson.core:jackson-databind:2.13.3'),
        @Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.3'),
        @Grab('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3')])
@com.cloudbees.groovy.cps.NonCPS

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

properties([parameters([string(name: 'EVENT_NAME', defaultValue: 'push', description: ''),
                        string(name: 'REF', defaultValue: 'main', description: ''),
                        string(name: 'PROJECT_NAME', defaultValue: 'Sample-Project', description: ''),
                        string(name: 'EXECUTION_ENVIRONMENT', defaultValue: 'kubernetes', description: ''),
                        string(name: 'GIT_URL', defaultValue: 'ssh://jenkins@ssh.gerrit.home.arpa:29418', description: '')])])

def onKubernetes(body) {
    if (params.EXECUTION_ENVIRONMENT == "kubernetes") {
        body.call()
    }
}

def onDocker(body) {
    if (params.EXECUTION_ENVIRONMENT == "docker") {
        body.call()
    }
}

def globalExecutor(body) {
    onKubernetes {
        podTemplate {
            node(POD_LABEL) {
                body.call()
            }
        }
    }
    onDocker {
        node {
            body.call()
        }
    }
}

def runner(image, body) {
    onKubernetes {
        podTemplate(yaml: """\
                    kind: Pod
                    spec:
                      containers:
                        - name: runner
                          image: ${image}
                          imagePullPolicy: Always
                          securityContext:
                            privileged: true
                          command:
                            - sleep
                          args:
                            - 9999999""".stripIndent()) {
            node(POD_LABEL) {
                container("runner") {
                    body.call()
                }
            }
        }
    }
    onDocker {
        docker.image(image).inside { 
            body.call()
        }
    }
}

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

// FIXME
def enrich(data) {
    def engine = new groovy.text.SimpleTemplateEngine()
    def tempParams = [:]
    tempParams.putAll(params)
    return engine.createTemplate(data.toString()).make(tempParams).toString()
}

def run(it, additionalEnvironmentVariables, actionsDirectory) {
    def uses = it['uses']
    def run = it['run']

    if (uses) {
        def result = loadAction(uses, it, additionalEnvironmentVariables, actionsDirectory)
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

    return stepDescription ? "${stepName} - ${stepDescription}" : stepName
}

def loadAction(name, context, additionalEnvironmentVariables, actionsDirectory) {

    binding.variables["with"] = context["with"]
    binding.variables["uses"] = context["uses"]
    binding.variables["additionalEnvironmentVariables"] = additionalEnvironmentVariables
    binding.variables["params"] = params
    binding.variables["stepName"] = getStepName(context)

    return load("${actionsDirectory}/${name}.groovy")
}

def prepareWorkflow(workflowFile, mapper) {
    def workflow = parseYaml(readFile(workflowFile.toString()), mapper)
    def action = {
        if (!handles(workflow['on'])) {
            print "Event not handled"
            return
        }
        workflow['jobs'].each { jobName, job ->
            runner(job['runs-on']) {
                def actionsDirectory = pwd(tmp: true)
                def additionalEnvironmentVariables = [:]

                withCredentials([sshUserPrivateKey(credentialsId: 'gerrit', keyFileVariable: 'ssh_key', usernameVariable: 'ssh_username')]) {
                    def uri = new URI("${params.GIT_URL}")
                    sh 'mkdir --parents ~/.ssh && cat $ssh_key >> ~/.ssh/id_rsa && chmod 0600 ~/.ssh/id_rsa'
                    //sh 'ssh-keyscan -H ' + uri.getHost() + ' 2>/dev/null >> ~/.ssh/known_hosts'
                    additionalEnvironmentVariables["sshUser"] = sh(script: 'printf $ssh_username', returnStdout: true).trim()
                }
                dir (actionsDirectory) {
                    checkout([$class: 'GitSCM',
                        branches: [[name: "${params.REF}"]],
                        extensions: [[$class: 'CloneOption', shallow: true]],
                        userRemoteConfigs: [[credentialsId:  'gerrit',
                                            url: "${params.GIT_URL}/Jenkins-Actions"]]])
                    // sh 'rm -v !("Pipeline")'
                    sh 'cp -RT Pipeline/src/main/groovy/eu/venthe/jenkins ./'
                    // sh 'rm -r Pipeline'
                    // sh 'rm -r pipelines'
                }
                stage("[${workflow['name']}/${jobName}]") {
                    echo job['steps'].toString();
                    job['steps'].each {
                        echo it.toString();
                        withEnv(prepareEnvVariablesForWithEnv(additionalEnvironmentVariables)) { ->
                            run(it, additionalEnvironmentVariables, actionsDirectory)
                        }
                    }
                }
            }
        }
    };
    return ["${workflow['name']}: ${workflowFile.name}":action];
}

globalExecutor {
    def workflows;
    stage("Prepre workflows") {
        checkout([$class: 'GitSCM',
            branches: [[name: "${params.REF}"]],
            extensions: [[$class: 'CloneOption', shallow: true]],
            userRemoteConfigs: [[credentialsId:  'gerrit',
                                url: "${params.GIT_URL}/${params.PROJECT_NAME}"]]])
        workflows = findFiles(glob: '.jenkins/workflows/*.y*ml').collectEntries { prepareWorkflow(it, mapper) }
        workflows.failFast = false;
    }
    parallel workflows
}