#!/usr/bin/env bash

set -o errexit
set -o pipefail

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
  echo >&2 "[${project_name}] Deleting project"
  actOnGerrit \
    delete-project delete --yes-really-delete --force \
    "${project_name}"
}

function loadProject() {
  local project_directory="${1}"
  local project_name="${2}"
  local current_directory="${PWD}"

  echo >&2 "[${project_name}] Copy code to temporary directory"
  local temp_directory="$(mktemp -d)"
  trap 'rm -rf -- "${temp_directory}"' EXIT

  echo >&2 "[${project_name}] Preparing repository from ${project_directory}"
  rsync --recursive -P --exclude=node_modules "${project_directory}" "${temp_directory}"
  cd ${temp_directory}
  git init
  git commit --allow-empty --message "Initial commit"
  git add --all
  git commit --message "Add initial code" || true

  echo >&2 "[${project_name}] Creating project in gerrit"
  actOnGerrit \
    gerrit create-project \
    --branch=${GERRIT_BRANCH} \
    "${project_name}.git"
  echo >&2 "[${project_name}] Pushing to gerrit"
  git push ssh://${GERRIT_USERNAME}@${GERRIT_URL}:29418/${project_name} HEAD:refs/heads/${GERRIT_BRANCH} --force
  cd "${current_directory}"
}

function install_update() {
  deleteProject "${2}" || true
  loadProject "${1}/" "${2}"
}

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
  install_update ./projects/Bootstrap-Repositories/Example-Service Example-Service
  install_update ./projects/Bootstrap-Repositories/jenkins Jenkins-Libraries
  install_update ./projects/Experiments/Deployment-Configuration/ArgocdExampleService ArgocdExampleService
  install_update ./projects/Experiments/Deployment-Configuration/State-Repository State-Repository
fi

