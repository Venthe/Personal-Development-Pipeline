#!/usr/bin/env bash

set -o errexit
set -o pipefail

DOCKER_IO_TAG="docker.io/venthe/ubuntu-runner:22.10"
LOCAL_DOCKER_TAG="docker.home.arpa/venthe/ubuntu-runner:22.10"
RUNNER_IMAGE="docker.io/library/ubuntu:22.04"
NODE_VERSION=19
HOME_REGISTRY="https://docker.home.arpa"

function prepare() {
  npm ci
}

function buildManager() {
  npm run build
}

function buildContainer() {
  DOCKER_BUILDKIT=0 docker build \
    --progress=plain \
    --tag="${DOCKER_IO_TAG}" \
    --tag="${LOCAL_DOCKER_TAG}" \
    --file=Dockerfile \
    ./dist
}

function test() {
  buildManager

  docker run \
    --rm \
    --volume ${PWD}/test/INPUT:/runner/metadata/event.json \
    --volume ${PWD}/test/test.sh:/test.sh \
    --volume ${PWD}/dist/index.js:/runner/index.js \
    --volume ${PWD}/dist/sourcemap-register.js:/runner/sourcemap-register.js \
    --volume /var/run/docker.sock:/var/run/docker.sock \
    --volume "${HOME}/.ssh/:/root/.ssh_test:ro" \
    --volume "${PWD}/test/env:/runner/metadata/env:ro" \
    --volume "${PWD}/test/secrets:/runner/metadata/secrets:ro" \
    --volume "${HOME}/.kube/config:/root/.kube/config:ro" \
    --env VPIPELINE_BUILD_ID="1" \
    --env VPIPELINE_GERRIT_URL="ssh://admin@host.docker.internal:29418" \
    --env VPIPELINE_JOB_NAME="Explore-GitHub-Actions" \
    --env VPIPELINE_NEXUS_URL="http://host.docker.internal:8081" \
    --env VPIPELINE_SECRET_NAME="pipeline" \
    --env VPIPELINE_DOCKER_URL="http://host.docker.internal:5000" \
    --env VPIPELINE_WORKFLOW=".pipeline/workflows/build.yml" \
    --env GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" \
    --interactive \
    --entrypoint bash \
    --tty \
    "${DOCKER_IO_TAG}" \
    /test.sh
}

function build() {
  buildManager && buildContainer
}

function publishContainer() {
  docker login "${HOME_REGISTRY}"
  docker push "${LOCAL_DOCKER_TAG}"
}

function publish() {
  build
  publishContainer
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
