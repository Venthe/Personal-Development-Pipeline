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

function uploadAll() {
  upload "buildkit-v0.11.2.linux-amd64.tar.gz" docker
  upload "docker-20.10.22.tgz" docker
  upload "nerdctl-1.1.0-linux-amd64.tar.gz" docker
  upload "gradle-7.6-all.zip" java/gradle
  upload "kubectl-linux-adm64-v1.26.0" kubernetes
  upload "zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz" java/jdk
  upload "helm-v3.11.0-linux-amd64.tar.gz" kubernetes
}

function downloadAll() {
  download "buildkit-v0.11.2.linux-amd64.tar.gz" docker
  download "docker-20.10.22.tgz" docker
  download "nerdctl-1.1.0-linux-amd64.tar.gz" docker
  download "gradle-7.6-all.zip" java/gradle
  download "kubectl-linux-adm64-v1.26.0" kubernetes
  download "zulu17.38.21-ca-jdk17.0.5-linux_x64.tar.gz" java/jdk
  download "helm-v3.11.0-linux-amd64.tar.gz" kubernetes
}

${@}
