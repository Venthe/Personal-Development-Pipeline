#!/usr/bin/bash

. .env

function create_label() {
    set -e
    local label_name="${1}"
    local data="${2}"
    ./_rest.sh rest PUT "a/projects/All-Projects/labels/${label_name}" \
    --header "Content-Type: application/json" \
    --data "${data}"
}

function push_project() {
    set -e
    PROJECT_NAME="${1}"
    ORIGIN_PATH="${2}"
    PRIVATE_KEY_PATH="${3:-~/.ssh/id_rsa}"
    ORIGINAL_PWD=$(pwd)

    cd /tmp
    sudo rm -rf ./${PROJECT_NAME}
    GIT_SSH_COMMAND="ssh -i ${PRIVATE_KEY_PATH}" git clone ssh://${USER}@${URL}:29418/${PROJECT_NAME}
    chmod 0777 ./${PROJECT_NAME}
    cd ${PROJECT_NAME}
    rsync -av "${ORIGIN_PATH}" "./" --exclude ".vagrant/"
    GIT_SSH_COMMAND="ssh -i ${PRIVATE_KEY_PATH}" git add --all
    GIT_SSH_COMMAND="ssh -i ${PRIVATE_KEY_PATH}" git commit -m "Initial commit"
    GIT_SSH_COMMAND="ssh -i ${PRIVATE_KEY_PATH}" git push --set-upstream origin master
    cd /tmp
    sudo rm -rf ./${PROJECT_NAME}
    cd ${ORIGINAL_PWD}
}

function provide_project() {
    set -e
    PROJECT_NAME="${1}"
    ORIGIN_PATH="${2}"
    PRIVATE_KEY_PATH="${3:-~/.ssh/id_rsa}"
    shift 2
    ./_rest.sh rest PUT a/projects/${PROJECT_NAME} --header "Content-Type: application/json" --data '{}'
    push_project "${PROJECT_NAME}" "${ORIGIN_PATH}" "${PRIVATE_KEY_PATH}" "${@}"
}

"${@}"
