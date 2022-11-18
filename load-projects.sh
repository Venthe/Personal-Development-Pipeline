#!/usr/bin/env bash

set -o errexit

SSH_DIR="$HOME/.ssh"
GIT_DIR="$HOME/.gitconfig"
GERRIT_URL="ssh.gerrit.home.arpa"
GERRIT_USERNAME="jjuly"
GERRIT_BRANCH="main"

function actOnGerrit() {
    ssh -p 29418 \
        -o "StrictHostKeyChecking no" \
        -i ~/.ssh/id_rsa \
        ${GERRIT_USERNAME}@${GERRIT_URL} \
        ${@}
}

function deleteProject() {
    local project_name="${1}"
    >&2 echo "[${project_name}] Deleting project"
    actOnGerrit \
        delete-project delete --yes-really-delete --force \
            "${project_name}"
}

function loadProject() {
    local project_directory="${1}"
    local project_name="${2}"
    local current_directory="${PWD}"

    >&2 echo "[${project_name}] Copy code to temporary directory"
    local temp_directory="$(mktemp -d)"
    trap 'rm -rf -- "${temp_directory}"' EXIT

    >&2 echo "[${project_name}] Preparing repository from ${project_directory}"
    cp -RT "${project_directory}" "${temp_directory}"
    cd ${temp_directory}
    git init
    git commit --allow-empty --message "Initial commit"
    git add --all
    git commit --message "Add initial code"

    >&2 echo "[${project_name}] Creating project in gerrit"
    actOnGerrit \
        gerrit create-project \
            --branch=${GERRIT_BRANCH} \
            "${project_name}.git"
    >&2 echo "[${project_name}] Pushing to gerrit"
    git push ssh://${GERRIT_USERNAME}@${GERRIT_URL}:29418/${project_name} HEAD:refs/heads/${GERRIT_BRANCH} --force
    cd "${current_directory}"
}

# loadProject ./worker-images/cfssl cfssl
# loadProject ./worker-images/project-loader project-loader
deleteProject Jenkins-Actions || true
deleteProject Sample-Project || true

loadProject ./projects/Jenkins-Actions/Jenkins-Actions Jenkins-Actions
loadProject ./projects/Jenkins-Actions/Sample-Project Sample-Project
