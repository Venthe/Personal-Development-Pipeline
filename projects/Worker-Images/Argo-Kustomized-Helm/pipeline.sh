#!/usr/bin/env bash

set -o errexit
set -o pipefail

IMAGE_VERSION=1.0.24
NAMESPACE=infrastructure

function tag() {
  printf docker.home.arpa/venthe/argocd-kustomized-helm-cmp:${1}
}

TAG=`tag ${IMAGE_VERSION}`

function build_container() {
  docker build . \
    --tag="${TAG}" \
    --tag="`tag latest`" \
    --build-arg image_version=${IMAGE_VERSION} \
    --file Dockerfile
}

function deploy_container() {
  docker login docker.home.arpa
  docker push "${TAG}"
}

function load_test_parameters() {
  local TEST_NAME="${1}"

  yq --exit-status --indent=0 --output-format="json" '.spec.source.plugin.parameters' ${TEST_NAME}
}

function test_container() {
  local TEST_NAME="./test/${1:-1}.yml"
  local ARGOCD_APP_NAME=`yq --unwrapScalar --exit-status '.metadata.name' ${TEST_NAME}`
  local ARGOCD_APP_NAMESPACE=`yq --unwrapScalar --exit-status '.metadata.namespace' ${TEST_NAME}`
  local ARGOCD_APP_SOURCE_REPO_URL=`yq --unwrapScalar --exit-status '.spec.source.repoURL' ${TEST_NAME}`
  local ARGOCD_APP_SOURCE_TARGET_REVISION=`yq --unwrapScalar --exit-status '.spec.source.targetRevision' ${TEST_NAME}`
  local DEBUG_CHART_NAME=`yq --unwrapScalar --exit-status '.spec.source.chart' ${TEST_NAME}`
  local ARGOCD_APP_PARAMETERS=`load_test_parameters ${TEST_NAME}`

  # --volume ${PWD}/plugin.yml:/home/argocd/cmp-server/config/plugin.yml \
  # --volume ${PWD}/manager.sh:/usr/local/bin/manager.sh \
  docker run \
    --interactive \
    --tty \
    --env DEBUG=1 \
    --env ARGOCD_APP_NAME="${ARGOCD_APP_NAME}" \
    --env ARGOCD_APP_NAMESPACE="${ARGOCD_APP_NAMESPACE}" \
    --env ARGOCD_APP_SOURCE_REPO_URL="${ARGOCD_APP_SOURCE_REPO_URL}" \
    --env ARGOCD_APP_SOURCE_TARGET_REVISION="${ARGOCD_APP_SOURCE_TARGET_REVISION}" \
    --env DEBUG_CHART_NAME="${DEBUG_CHART_NAME}" \
    --env KUBE_VERSION="1.20.1" \
    --env KUBE_API_VERSIONS="1.20.1" \
    --env ARGOCD_APP_PARAMETERS="${ARGOCD_APP_PARAMETERS}" \
    --rm \
    ${TAG} \
    2>/dev/null
}

function deploy_application() {
  kubectl --namespace="${NAMESPACE}" apply -f ./test/${1}.yml
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
else
  build_container
  deploy_container
fi
