#!/usr/bin/bash

HOSTNAME="gerrit.local"
HTTPD_LISTEN_URL="proxy-http://*:8080/"
CANONICAL_WEB_URL="http://${HOSTNAME}/"
CANONICAL_GIT_URL="git://${HOSTNAME}/"
NAMESPACE="gerrit"
RELEASE_NAME="gerrit"

helm repo add venthe https://raw.githubusercontent.com/Venthe/k8s-gerrit/various-updates/chart-repository/
helm repo update

function compileValuesFile() {
  function prepareVariables() {
    docker run --rm --interactive \
      imega/jq --null-input \
      --arg HTTPD_LISTEN_URL "${HTTPD_LISTEN_URL}" \
      --arg CANONICAL_WEB_URL "${CANONICAL_WEB_URL}" \
      --arg CANONICAL_GIT_URL "${CANONICAL_GIT_URL}" \
      --arg HOSTNAME "${HOSTNAME}" \
      '
      {}
      |.httpd.listenUrl |= $HTTPD_LISTEN_URL
      |.gerrit.canonicalWebUrl |= $CANONICAL_WEB_URL
      |.gerrit.canonicalGitUrl |= $CANONICAL_GIT_URL
      |.gerrit.ingress.host |= $HOSTNAME
      ' \
      > "./tmp/variables.json"
  }
  prepareVariables

  docker run --interactive --rm \
    --volume="$(pwd)/templates:/templates" \
    --volume="$(pwd)/tmp/:/variables" \
    dinutac/jinja2docker:latest --format=yaml \
    /templates/values.yaml.j2 /variables/variables.json \
    > ./tmp/values.yaml
}

function installChart() {
  helm upgrade --install \
    --namespace="${NAMESPACE}" --create-namespace \
    --values="$(pwd)/tmp/values.yaml" \
    "${RELEASE_NAME}" \
    venthe/gerrit
}

function install() {
  compileValuesFile
  installChart
}

function installWithPatchedValues() {
  NODE_PORT=$(kubectl get service --namespace=gerrit gerrit-gerrit-service --output=jsonpath='{.spec.ports[0].nodePort}')
  HOSTNAME="localhost"
  CANONICAL_WEB_URL="http://${HOSTNAME}:${NODE_PORT}/"
  install
}

install
installWithPatchedValues
