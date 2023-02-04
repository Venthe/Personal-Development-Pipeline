#!/usr/bin/env bash

set -o pipefail
set -o errexit

. ./.env

list_tags() {
  ansible-playbook ./provision-kubernetes.yml --list-tags
}

function provision() {
  ansible-playbook \
    ./provision-kubernetes.yml \
    --inventory-file ./kubernetes.inventory.yml \
    --user ${PLAY_USERNAME} \
    --ask-become \
    "${@}"
}

function ansible_lint() {
  local filename="${1}"
  ansible-lint "${filename}"
}

function yaml_lint() {
  local filename="${1}"
  yamllint "${filename}"
}

function ansible_syntax_check() {
  local filename="${1}"
  ansible-playbook "${filename}" --syntax-check
}

function validate() {
  files=$(find . -type f | grep -e yml -e yaml)
  for file in $files; do
    echo "${file}"
    ansible_syntax_check "${file}"
  done
}

function lint() {
  function updateStatus() {
    local newStatus=$1
    if [[ $newStatus -gt 0 ]]; then
      status=1
    fi
  }

  files=$(find . -type f | grep -e yml -e yaml)

  status=0
  for file in $files; do
    echo "${file}"

    local filename="${1}"
    ansible_lint "${filename}"
    updateStatus $?
    yaml_lint "${filename}"
    updateStatus $?
  done
  exit $status
}

${@}
