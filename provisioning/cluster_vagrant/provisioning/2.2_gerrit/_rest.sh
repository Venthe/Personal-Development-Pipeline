#!/bin/env bash

. .env

function rest() {
    HTTP_BASIC_AUTH=$(printf ${USER}:${PASSWORD} | base64)
    local method="${1}"
    local path="${2}"
    local url="${URL}/${path}"
    shift 2
    curl --request "${method}" \
         --show-error \
         --header "Authorization: Basic ${HTTP_BASIC_AUTH}" \
         "${url}" "${@}" | sed '0,/)]}/ d'
}

function login() {
    rest POST login/ \
        --header 'Conent-Type: application:x-www-form-urlencoded' \
        --data "username=${USER}&password=${PASSWORD}"
}

"${@}"
