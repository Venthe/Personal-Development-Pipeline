#!/usr/bin/env bash

set -o errexit
set -o pipefail

ARGOCD_NAMESPACE=infrastructure

function render_release_value_files() {
  local namespace="${1}"

  if [[ -z ${namespace} ]]; then
    echo >&2 "Namespace is required for rendering"
    exit 1
  fi

  cat "./environment/${namespace}/applications.yml" "./globals.yml" |
    yq -o=json ". | .global.namespace=\"${namespace}\"" |
    jinja2 --strict "./values-template.yml"
}

function deploy() {
  local namespace="${1}"

  helm upgrade \
    --install \
    --namespace="${ARGOCD_NAMESPACE}" \
    --values=<(render_release_value_files "${namespace}" | yq) \
    "argocd-${namespace}" \
    "./deployment"
}

function template() {
  local namespace="${1}"
  echo >&2 "Templating for ${namespace}"

  if [[ -z ${namespace} ]]; then
    echo >&2 "Namespace is required for templating"
    exit 1
  fi

  # --validate \
  helm template \
    --namespace="${ARGOCD_NAMESPACE}" \
    --debug \
    --values=<(render_release_value_files "${namespace}" | yq) \
    "argocd-${namespace}" \
    "./deployment"
}

function updateHelmProperty() {
  local namespace="${1}"
  local applicationName="${2}"
  local propertyKey="${3}"
  local propertyValue="${4}"

  if [[ -z ${namespace} ]]; then
    echo >&2 "Namespace is required for updating helm property"
    exit 1
  fi

  if [[ -z ${applicationName} ]]; then
    echo >&2 "Application name is required for updating helm property"
    exit 1
  fi

  if [[ -z ${propertyKey} ]]; then
    echo >&2 "Property key is required for updating helm property"
    exit 1
  fi

  if [[ -z ${propertyValue} ]]; then
    echo >&2 "Property value is required for updating helm property"
    exit 1
  fi

  yq -i -e ". | (.helm.${applicationName} | .properties.${propertyKey}) = \"${propertyValue}\"" "./environment/${namespace}/applications.yml"
}

function deployAll() {
  for d in ./environment/*/; do
    deploy $(basename $d)
  done
}

function push() {
  git add --all &&
    git commit -m "${1}" &&
    git push --set-upstream origin ${2}
}

function fix_managed_annotations() {
  local namespace="${1}"
  local resource="${2}"
  shift 2
  kubectl annotate --namespace="${namespace}" "${resource}" "${@}"
}

function updateHelmChartVersion() {
  local namespace="${1}"
  local applicationName="${2}"
  local chartVersion="${3}"

  if [[ -z ${namespace} ]]; then
    echo >&2 "Namespace is required for updating helm chart version"
    exit 1
  fi

  if [[ -z ${applicationName} ]]; then
    echo >&2 "Application name is required for updating helm chart "
    exit 1
  fi

  if [[ -z ${chartVersion} ]]; then
    echo >&2 "Chart version is required for updating helm chart "
    exit 1
  fi

  yq -i -e ". | (.helm.${applicationName} | .chartRevision) = \"${chartVersion}\"" "./environment/${namespace}/applications.yml"
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
else
  exit 1
fi

# ./manager.sh updateHelmChartVersion integration argocd-example 1.0.1
# ./manager.sh updateHelmProperty integration argocd-example imageTag 20230205-190544-a467a10