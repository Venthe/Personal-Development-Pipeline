#!/usr/bin/env bash

set -o errexit
set -o pipefail

root="../../../../"
dir=$(pwd)

function project() {
  local action="${1}"
  local else="${@:2}"
  (cd "${root}" && bash ./load-projects.sh "${action}" ${else})
}

function test() {
  local action="${1}"
  local else="${@:2}"
  (cd "${root}" && echo "[${action}][${else}]")
}

function cleanPipeline() {
  rm -rf ./node_modules/@pipeline
  npm install
}

function buildLibraries() {
  (cd ../libraries && bash manager.sh)
  cleanPipeline
}

function buildSingle() {
  local d="${1}"

  cd "${d}"
    cleanPipeline
    # npm run build --ws
    npm install
    npm run build
  cd "${dir}"

  action="actions/${d%/}"
  project deleteProject "${action}" || true
  project loadProject "${dir}/${d}/" "${action}"
}


if [[ "${REBUILD_LIBRARIES}" -eq "1" ]]; then
  buildLibraries
fi

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
  for d in */; do
    buildSingle "${d}"
  done
fi

#(cd actions && bash ./manager.sh buildSingle setup-yq) && ./manager.sh test tests/argocd-deploy
#./manager.sh template actions/template
