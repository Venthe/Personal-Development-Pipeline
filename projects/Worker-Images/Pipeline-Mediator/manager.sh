#!/usr/bin/env bash

set -o errexit
set -o pipefail

NAMESPACE="infrastructure"
RELEASE_NAME="pipeline-mediator"
LATEST_TAG="latest"
IMAGE_NAME="docker.home.arpa/pipeline-mediator"

CURRENT_VERSION_TAG=`git rev-parse HEAD`
CURRENT_DATETIME=`date --utc +%Y%m%d-%H%M%S`
CURRENT_BRANCH=`git branch --show`

THIS_TAG="${CURRENT_BRANCH}-${CURRENT_DATETIME}-${CURRENT_VERSION_TAG}"

function getStatusOfTheRelease() {
  helm status --namespace="${NAMESPACE}" "${RELEASE_NAME}" >/dev/null 2>&1; echo $?
}

function build() {
  docker run \
    --rm \
    -u gradle \
    -v "$PWD":/home/gradle/project \
    -w "/home/gradle/project" \
    "docker.io/library/gradle:7.6.0-jdk17-alpine" \
    gradle build --no-daemon -x test
}

function buildImage() {
  docker build \
    --file Dockerfile \
    --tag "${IMAGE_NAME}:${THIS_TAG}" \
    ./build/libs
}

function deploy() {
  # docker login docker.home.arpa --username admin
  local tag=${1:-${THIS_TAG}}
  docker login docker.home.arpa
  docker push "${IMAGE_NAME}:${tag}"
}

function deployToCloud() {
  local currentStatus=`getStatusOfTheRelease`
  if [[ ${currentStatus} -ne 0 ]]; then
    helm upgrade "${RELEASE_NAME}" \
      ./charts \
      --namespace="${NAMESPACE}" \
      --install
  else
    # main-20230217-114819-c51dd6554d22d090b09fa4b6d95a372655e522fb
    local tag=${1:-${THIS_TAG}}
    helm upgrade "${RELEASE_NAME}" \
      ./charts \
      --namespace="${NAMESPACE}" \
      --values="./.values.yaml" \
      --set="image.tag=${tag}" \
      --install
  fi
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
