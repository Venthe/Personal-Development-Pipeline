#!/usr/bin/env bash

set -o errexit
set -o pipefail

#Links (Local)
#_GERRIT_URL="ssh://admin@host.docker.internal:29418"
#_NEXUS_URL="http://host.docker.internal:8081/repository/raw"
#_DOCKER_URL="http://host.docker.internal:5000"
#_DOCKER_TAG="host.docker.internal:5000"

NAMESPACE="infrastructure"
RELEASE_NAME="pipeline-mediator"
LATEST_TAG="latest"
IMAGE_NAME="docker.home.arpa/pipeline-mediator"

CURRENT_VERSION_TAG=`git rev-parse HEAD`
CURRENT_DATETIME=`date --utc +%Y%m%d-%H%M%S`
CURRENT_BRANCH=`git branch --show`

THIS_TAG="${CURRENT_BRANCH}-${CURRENT_DATETIME}-${CURRENT_VERSION_TAG}"

#Links (Remote)
_GERRIT_URL="ssh://admin@ssh.gerrit.home.arpa:29418"
_DOCKER_URL="https://docker.home.arpa"
_NEXUS_URL="https://nexus.home.arpa/repository/raw-hosted"
_DOCKER_TAG="docker.home.arpa"

# Docker image details
RUNNER_BASE_IMAGE="docker.io/library/ubuntu:22.04"
NODE_VERSION=19

# Other
RUNNER_IMAGE="${_DOCKER_TAG}/venthe/ubuntu-runner:22.10"

function cleanPipeline() {
  rm -rf ./node_modules/@pipeline
  npm install
}

function buildActions() {
  (cd ./actions && bash ./manager.sh)
}

function buildAction() {
  (cd ./actions && bash ./manager.sh buildSingle "${1}")
}

function buildLibraries() {
  (cd ./libraries && bash ./manager.sh)
  cleanPipeline
  npm install
}

function buildLibrary() {
  (cd ./libraries && bash ./manager.sh buildSingle "${1}")
  cleanPipeline
  npm install
}

function prepare() {
  if [[ "${REBUILD_LIBRARIES}" -eq "1" ]]; then
    buildLibraries
  fi
  npm ci
}

function buildManager() {
  npm ci
  npm run build
}

function buildContainer() {
  DOCKER_BUILDKIT=0 docker build \
    --progress=plain \
    --tag="${RUNNER_IMAGE}" \
    --file=Dockerfile \
    ./dist
}

# REBUILD_MANAGER=1 ./manager.sh test tests/remote-actions
function test() {
  local path="${1}"
  local absolutePath="${PWD}/test/${path}"

  if [[ "${REBUILD_LIBRARIES}" -eq "1" ]]; then
    buildLibraries
  fi

  if [[ "${REBUILD_MANAGER}" -eq "1" ]]; then
    buildManager
  fi

  if [[ "${REBUILD_CONTAINER}" -eq "1" ]]; then
    buildContainer
  fi

  if [[ "${REBUILD_ACTIONS}" -eq "1" ]]; then
    buildActions
  fi

  function project() {
    (cd ../../../ && bash ./load-projects.sh "${@}" Test/ActionRunnerTestService)
  }

  project deleteProject || true
  project loadProject "${absolutePath}/repository/"

  # --interactive --tty
  docker run \
    --rm \
    --entrypoint bash \
    --volume "${HOME}/.kube/config:/root/.kube_test/config:ro" \
    --volume "${HOME}/.ssh/:/root/.ssh_test:ro" \
    --volume "${PWD}/dist/index.js:/runner/index.js" \
    --volume "${PWD}/dist/sourcemap-register.js:/runner/sourcemap-register.js" \
    --volume "${PWD}/dist/index.js.map:/runner/index.js.map" \
    --volume "${absolutePath}/env:/runner/metadata/env:ro" \
    --volume "${absolutePath}/event.yaml:/runner/metadata/event.yaml" \
    --volume "${absolutePath}/secrets:/runner/metadata/secrets:ro" \
    --volume "${PWD}/test/test.sh:/test.sh" \
    --volume "/var/run/docker.sock:/var/run/docker.sock" \
    --volume "/etc/ssl/certs/ca-certificates.crt:/etc/ssl/certs/ca-certificates.crt:ro" \
    \
    --env PIPELINE_JOB_NAME="TestedJob" \
    --env PIPELINE_BUILD_ID="1" \
    --env PIPELINE_DEBUG="1" \
    --env PIPELINE_WORKFLOW="workflow.yaml" \
    --env PIPELINE_NEXUS_URL="${_NEXUS_URL}" \
    --env PIPELINE_GERRIT_URL="${_GERRIT_URL}" \
    --env PIPELINE_DOCKER_URL="${_DOCKER_URL}" \
    \
    --env GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no" \
    "${RUNNER_IMAGE}" \
    /test.sh

  project deleteProject
}

function build() {
  buildManager && buildContainer
}

function publishContainer() {
  docker login docker.home.arpa
  docker push "${RUNNER_IMAGE}"
}

function full() {
  build
  publishContainer
}

prepareNPM() {
  npm set strict-ssl=false
  npm adduser --auth-type=legacy --registry https://nexus.home.arpa/repository/npm-hosted/
}

function testAll() {
    bash -c "find ./test | grep -E 'event\.ya?ml' | xargs dirname | sed 's/^\.\/test\///' | xargs -I{} bash ./manager.sh test {}"
}

if [[ ${#} -ne 0 ]]; then
  if declare -f "$1" >/dev/null; then
    # call arguments verbatim
    "${@}"
  else
    # Show a helpful error
    echo >&2 "'$1' is not a known function name"
    exit 1
  fi
fi
