#!groovy
job("CI/CD Pipeline") {
	description()
	keepDependencies(false)
	disabled(false)
	concurrentBuild(false)
	wrappers {
		timestamps()
	}
    pipeline {
        agent {
            kubernetes {
                defaultContainer 'jnlp'
                yaml '''
    spec:
    containers:
        - name: docker
        image: docker:19.03
        command:
            - cat
        tty: true
        privileged: true
        resources:
            limits:
                memory: "512Mi"
                cpu: "1"
        volumeMounts:
            - name: dockersock
            mountPath: /var/run/docker.sock
            - name: m2
            mountPath: /root/.m2
        - name: helm
        image: lachlanevenson/k8s-helm:v3.4.0
        resources:
            limits:
                memory: "512Mi"
                cpu: "1"
        command:
            - cat
        tty: true
    volumes:
        - name: dockersock
        hostPath:
            path: /var/run/docker.sock
        - name: m2
        hostPath:
            path: /root/.m2
    '''
            }
        }
        stages {
            stage('Source code management: Checkout') {
                steps {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "${params.Branch}"]],
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
                            credentialsId: '51446dcd-f074-498f-864d-9413105b1e42',
                            url: "http://gerrit-gerrit-service.gerrit/${params.Repository}"
                        ]]
                    ])
                }
            }
            stage('Jenkins: Perform in agents') {
                steps {
                    container('docker') {
                        sh "ps"
                        sh "docker ps"
                        sh "docker build ."
                    }
                }
            }
        }
        post {
            always {
                echo 'This will always run'
            }
            success {
                echo 'This will run only if successful'
            }
            failure {
                echo 'This will run only if failed'
            }
            unstable {
                echo 'This will run only if the run was marked as unstable'
            }
            changed {
                echo 'This will run only if the state of the Pipeline has changed'
                echo 'For example, if the Pipeline was previously failing but is now successful'
            }
        }
    }
}