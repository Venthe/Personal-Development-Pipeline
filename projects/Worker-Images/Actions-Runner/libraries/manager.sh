#!/usr/bin/env bash

set -o errexit
set -o pipefail

function build() {
  npm run build
}

function install() {
  npm install
}

function publish() {
  npm publish --registry https://nexus.home.arpa/repository/npm-hosted/
}

function buildSingle() {
  echo "Building ${1}"
  (cd "${1}" && install)
  (cd "${1}" && build)
  (cd "${1}" && publish)
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
  for d in */; do
    buildSingle "${d}"
  done
fi


