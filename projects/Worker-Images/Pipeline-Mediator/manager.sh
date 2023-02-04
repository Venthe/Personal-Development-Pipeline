#!/usr/bin/env bash

set -o errexit
set -o pipefail

TAG="docker.home.arpa/pipeline-mediator:latest"

function build() {
  docker run \
    --rm \
    -u gradle \
    -v "$PWD":/home/gradle/project \
    -w "/home/gradle/project" \
    "docker.io/library/gradle:7.6.0-jdk17-alpine" \
    gradle build --no-daemon
}

function buildImage() {
  docker build \
    --file Dockerfile \
    --tag "${TAG}" \
    ./build/libs
}

function deploy() {
  docker login docker.home.arpa --username admin
  docker push "${TAG}"
}

function deployToCloud() {
  helm upgrade pipeline-mediator \
    ./charts \
    --namespace=infrastructure \
    --install
}

function full() {
  build
  buildImage
  deploy
  deployToCloud
}

if [[ ${#} -ne 0 ]]; then
    if declare -f "$1" > /dev/null; then
        # call arguments verbatim
        "${@}"
    else
        # Show a helpful error
        >&2 echo "'$1' is not a known function name"
        exit 1
    fi
fi
