#!/usr/bin/env bash

set -o errexit
set -o pipefail

if [[ ${DEBUG} -eq "1" ]]; then
  set -o xtrace
fi

function app_parameters() {
  if [[ ${DEBUG} -eq "1" ]]; then
    echo "${ARGOCD_APP_PARAMETERS}" | 1>&2 yq --input-format=json --exit-status "${@}"
  fi
  echo "${ARGOCD_APP_PARAMETERS}" | yq --input-format=json --exit-status "${@}"
}

function save_values() {
  1>&2 echo "Save values"
  app_parameters '((.[] | select(.name == "values").string) | from_yaml) // {}'
}

function save_kustomizations() {
  1>&2 echo "Saving customizations"
  app_parameters '(.[] | select(.name == "patches").string) as $patches
  | {}
  | .apiVersion = "kustomize.config.k8s.io/v1beta1"
  | .resources = ["all.yaml"]
  | . *= (( $patches | from_yaml ) // {})
  '
}

function download_chart() {
  1>&2 echo "Downloading charts"
  helm repo add repo --insecure-skip-tls-verify "${ARGOCD_APP_SOURCE_REPO_URL}" > /dev/null
  helm repo update > /dev/null
  helm pull "repo/${DEBUG_CHART_NAME}" --untar --untardir=/workdir --destination=/workdir  --insecure-skip-tls-verify
  mv docker/* ./
  rm -r docker
  ls
}

function release_name() {
  app_parameters '.[] | select(.name == "helmReleaseName").string'
}

function template() {
  1>&2 echo "Template"
  helm template \
    --include-crds \
    --values="values-from-application.yml" \
    --release-name `release_name` \
    ./
}

function render_additional_manifests() {
  1>&2 echo "Render additional manifests"
  2>/dev/null app_parameters --exit-status=false '
  ((.[] | select(.name == "additionalManifests").array) // [])
  | .[]
  | from_yaml
  | split_doc
  ' || printf "{}"
}

function merge() {
  1>&2 echo "Merging files"
  yq --inplace ${@}
}

### Public

function init() {
  if [[ ${DEBUG} -eq "1" ]]; then
    ls && printenv
  fi
  save_values > values-from-application.yml
  save_kustomizations > kustomization.yml
  template > all.yaml
  render_additional_manifests > additional.yaml
  merge all.yaml additional.yaml
}

function generate() {
  kustomize build
}

function server() {
  1>&2 echo "Starting server"
  /var/run/argocd/argocd-cmp-server ${@}
}

### End Public

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
  download_chart
  init
  generate
fi
