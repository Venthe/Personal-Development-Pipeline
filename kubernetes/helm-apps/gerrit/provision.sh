#!/bin/env bash

function rest() {
    HTTP_PORT=$(kubectl get services -n gerrit -o=jsonpath='{.items[*].spec.ports[?(@.name=="http")].nodePort}')
    HTTP_BASIC_AUTH=$(printf jjuly:secret | base64)
    local method="${1}"
    local path="${2}"
    shift 2
    local url="gerrit.example.org/${path}"
    curl -X "${method}" --header "Authorization: Basic ${HTTP_BASIC_AUTH}" "${url}" "${@}"
}

function push_project() {
    PROJECT_NAME="${1}"
    ORIGIN_PATH="${2}"
    ORIGINAL_PWD=$(pwd)

    cd /tmp
    rm -rf ./${PROJECT_NAME}
    git clone ssh://admin@gerrit.example.org:29418/${PROJECT_NAME}
    cd ${PROJECT_NAME}
    cp -R ${ORIGINAL_PWD}/${ORIGIN_PATH}/* ./
    git add --all
    git commit -m "Initial commit"
    git push --set-upstream origin master

    cd ${ORIGINAL_PWD}
}

function provide_project() {
    PROJECT_NAME="${1}"
    ORIGIN_PATH="${2}"
    shift 2
    rest PUT a/projects/${PROJECT_NAME} --header "Content-Type: application/json" --data '{}'
    push_project "${PROJECT_NAME}" "${ORIGIN_PATH}" "${@}"

}

"${@}"