#!/usr/bin/env bash

set -o xtrace
set -o errexit

. ./.env

function download() {
    curl \
        --fail \
        "https://nexus.home.arpa/repository/raw-hosted/${2}/${1}" \
        --output "raw-binaries/${1}"
}

function upload() {
    curl \
        --fail \
        --user admin:secret \
        --upload-file "raw-binaries/${1}" \
        "https://nexus.home.arpa/repository/raw-hosted/${2}/${1}"
}

function upload_all() {
  upload "buildkit-v0.11.2.linux-amd64.tar.gz" docker
  upload "docker-20.10.22.tgz" docker
  upload "nerdctl-1.1.0-linux-amd64.tar.gz" docker
  upload "gradle-7.6-all.zip" java/gradle
  upload "kubectl-linux-adm64-v1.26.0" kubernetes
  upload "zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz" java/jdk
  upload "amazon-corretto-8.362.08.1-linux-x64.tar.gz" java/jdk
  upload "helm-v3.11.0-linux-amd64.tar.gz" kubernetes
  upload "yq_linux_amd64.tar.gz" misc
  upload "apache-maven-3.9.0-bin.tar.gz" java/maven
  upload "apache-maven-3.3.9-bin.tar.gz" java/maven
  upload "node-v18.15.0-linux-x64.tar.xz" node
  upload "node-v8.17.0-linux-x64.tar.gz" node
}

function download_all() {
  download "buildkit-v0.11.2.linux-amd64.tar.gz" docker
  download "docker-20.10.22.tgz" docker
  download "nerdctl-1.1.0-linux-amd64.tar.gz" docker
  download "gradle-7.6-all.zip" java/gradle
  download "kubectl-linux-adm64-v1.26.0" kubernetes
  download "zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz" java/jdk
  download "amazon-corretto-8.362.08.1-linux-x64.tar.gz" java/jdk
  download "helm-v3.11.0-linux-amd64.tar.gz" kubernetes
  download "apache-maven-3.9.0-bin.tar.gz" java/maven
  download "apache-maven-3.3.9-bin.tar.gz" java/maven
  download "node-v18.15.0-linux-x64.tar.xz" node
  download "node-v8.17.0-linux-x64.tar.gz" node
}

${@}
