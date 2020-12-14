 pipeline {
        agent {
            kubernetes {
                defaultContainer 'jnlp'
                yaml '''\
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                    - name: docker
                      image: docker:20.10.0-dind
                      securityContext:
                        privileged: true
                '''.stripIndent()
            }
        }
        stages {
            stage('Jenkins: Perform in agents') {
                steps {
                    git credentialsId: 'a189192c-1ebf-4d14-b875-038064f3a333',
                        url: 'http://admin@gerrit-gerrit-service.gerrit:80/a/Multi'
                    container('docker') {
                        sh 'docker image ls --all'
                        sh 'docker login --password=admin123 --username=admin nexus-sonatype-nexus.nexus:1212'
                        sh 'docker pull scratch'
                        sh 'docker tag scratch nexus-sonatype-nexus.neus:1212/scratch'
                        sh 'docker push nexus-sonatype-nexus.neus:1212/scratch'
                        sh 'docker image ls --all'
                        // sh 'ls'
                        // sh '''\
                        //       chmod +x ./build.sh
                        //       chmod 0777 ./build.sh
                        //       stat ./build.sh
                        //       sh ./build.sh
                        //       '''.stripIndent()
                    }
                }
            }
        }
 }