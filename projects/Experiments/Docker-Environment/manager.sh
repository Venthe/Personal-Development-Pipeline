#!/usr/bin/env bash

set -o errexit
set -o pipefail

. ./.env

function push() {
  docker login
}

function buildMediator() {
    cd Gerrit-To-Bus-Mediator
    npm run build
    #NODE_DEBUG=cluster,net,http,fs,tls,module,timers
    # node ./dist/index.mjs
}

function generate() {
    cat contexts.schema.yaml \
        | yq --no-colors --output-format=json eval \
        | generate-json /dev/stdin \
        | yq eval --prettyPrint --output-format=yaml \
        ${@}
}

function lint() {
  yamllint "./docker-compose.yml"
  docker compose config >/dev/null
  shellcheck "./*.sh"
}

function jenkinsInitialPassword() {
  docker exec --interactive --tty jenkins cat /var/jenkins_home/secrets/initialAdminPassword
}

function installLintDependencies() {
sudo apt install shellcheck ansiblelint --assume-yes
}

function up() {
    docker compose up -d --build
}

function deploy() {
  cd Gerrit-To-Bus-Mediator
  docker run \
    --rm \
    -u gradle \
    -v "$PWD":/home/gradle/project \
    -w "/home/gradle/project" \
    "docker.io/library/gradle:7.6.0-jdk11-alpine" \
    gradle build
  docker build . --tag docker.home.arpa/build-mediator:latest
  docker login docker.home.arpa --username admin
  docker push docker.home.arpa/build-mediator:latest
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
