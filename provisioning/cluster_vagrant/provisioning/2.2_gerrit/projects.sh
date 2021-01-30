#!/usr/bin/bash

set -e

. .env

. ./_rest.sh

function create_label() {
    local label_name="${1}"
    local data="${2}"
    rest PUT "a/projects/All-Projects/labels/${label_name}" \
    --header "Content-Type: application/json" \
    --data "${data}"
}

function push_project() {
    local project_name="${1}"
    local origin_path="${2}"
    local private_key_path="${3:-~/.ssh/id_rsa}"
    local original_directory=$(pwd)

    cd /tmp
    sudo rm -rf ./${project_name}
    GIT_SSH_COMMAND="ssh -i ${private_key_path}" git clone ssh://${GERRIT_USER}@${GERRIT_URL}:29418/${project_name}
    chmod 0777 ./${project_name}
    cd ${project_name}
    rm -rf ./*
    rsync -av "${origin_path}" "./" --exclude ".vagrant/"
    git add --all
    git commit --message "Initial commit" || true
    GIT_SSH_COMMAND="ssh -i ${private_key_path}" git push --set-upstream origin master --force
    cd /tmp
    sudo rm -rf ./${project_name}
    cd ${original_directory}
}

function provide_project() {
    local project_name="${1}"
    local origin_path="${2}"
    local private_key_path="${3:-~/.ssh/id_rsa}"
    shift 3
    rest PUT a/projects/${project_name} --header "Content-Type: application/json" --data '{}' || true
    push_project "${project_name}" "${origin_path}" "${private_key_path}" "${@}"
}

if [[ ${#} -ne 0 ]]; then
    "${@}"
fi
