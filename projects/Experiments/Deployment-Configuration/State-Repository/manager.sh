#!/usr/bin/env bash

function render() {
  local namespace="${1}"
  cat "./environment/${namespace}/applications.yml" |
    yq -o=json ". | .global.namespace=\"${namespace}\"" |
    jinja2 "./values-template.yml"
}

function deploy() {
  local namespace="${1}"

  helm upgrade \
    --install \
    --namespace="infrastructure" \
    --values=<(render "${namespace}" | yq) \
    "argocd-${namespace}" \
    "./deployment"
}

function template() {
  local namespace="${1}"

  helm template \
    --namespace="infrastructure" \
    --debug \
    --values=<(render "${namespace}" | yq) \
    "argocd-${namespace}" \
    "./deployment"
}

export -f deploy
export -f render
export -f template

if [[ ${#} -ne 0 ]]; then
  if declare -f "$1" >/dev/null; then
    # call arguments verbatim
    "${@}"
  else
    # Show a helpful error
    echo >&2 "'$1' is not a known function name"
    exit 1
  fi
else
  ls ./environment -1 | xargs -I{} bash -c 'deploy {}'
fi
