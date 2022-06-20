#!/bin/env bash

set -e

function rest() {
    local basic_auth=$(printf ${GERRIT_USER}:${GERRIT_PASSWORD} | base64)
    local method="${1}"
    local path="${2}"
    shift 2
    local url="${GERRIT_URL}/${path}"
    >&2 echo "* ${method} '${url}'"
    curl --request "${method}" \
         --show-error \
         --fail \
         --silent \
         --header "Authorization: Basic ${basic_auth}" \
         "${url}" "${@}" \
         | sed '0,/)]}/ d'
}

function login() {
    rest POST "login/" \
        --header 'Content-Type: application:x-www-form-urlencoded' \
        --data "username=${GERRIT_USER}&password=${GERRIT_PASSWORD}"
}

if [[ ${#} -ne 0 ]]; then
    "${@}"
fi
