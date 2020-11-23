#!groovy
job('Seed job DSL') {
    checkout([
        $class: 'GitSCM',
        branches: [[name: "master"]],
        doGenerateSubmoduleConfigurations: false,
        extensions: [[
            $class: 'CloneOption',
            depth: 1,
            noTags: true,
            reference: '',
            shallow: true
        ]],
        submoduleCfg: [],
        userRemoteConfigs: [[
            credentialsId: 'jenkins-git',
            url: "http://gerrit-gerrit-service.gerrit/Jenkins"
        ]]
    ])

    triggers {
        scm("H/5 * * * *") {
            ignorePostCommitHooks(false)
        }
    }
    steps {
        jobDsl {
            targets 'jobs/**/*.groovy'
        }
    }
    wrappers {
        timestamps()
    }
}