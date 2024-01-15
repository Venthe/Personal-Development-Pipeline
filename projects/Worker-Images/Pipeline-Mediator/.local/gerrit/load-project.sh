#!/usr/bin/env bash

GERRIT_URL="localhost:1555"
GERRIT_PROTOCOL="http"
GERRIT_USERNAME="admin"
GERRIT_PASSWORD="secret"

function gerrit_allow_force_update() {
  echo "Resetting permissions for All-Projects"

  local WORK_DIR=$(mktemp -d)
  originalPwd="${PWD}"
  cd "${WORK_DIR}"

  git clone "${GERRIT_PROTOCOL}://${GERRIT_USERNAME}:${GERRIT_PASSWORD}@${GERRIT_URL}/a/All-Projects" .
  git config user.name "Admin"
  git config user.email "admin@example.com"
  git fetch origin "refs/meta/config:refs/remotes/origin/meta/config"
  git checkout "meta/config"

  sed -i 's/push = group/push = +force group/' project.config
  git add project.config
  git commit -m "Allow unrestricted force push"
  git push origin meta/config:refs/meta/config

  cd "${originalPwd}"
  rm -rf "${WORK_DIR}"
}

function load_project() {
  local PROJECT_NAME="${1}"
  echo "Uploading project ${PROJECT_NAME}"

  echo "Deleting gerrit project"
  curl "${GERRIT_PROTOCOL}://${GERRIT_URL}/a/projects/${PROJECT_NAME}" \
    -u "${GERRIT_USERNAME}:${GERRIT_PASSWORD}" \
    -X DELETE

  echo "Creating gerrit project"
  curl "${GERRIT_PROTOCOL}://${GERRIT_URL}/a/projects/${PROJECT_NAME}" \
    -u "${GERRIT_USERNAME}:${GERRIT_PASSWORD}" \
    -X PUT \
    -H "Content-Type: application/json; charset=UTF-8" \
    -d '{}'

  local WORK_DIR=$(mktemp -d)

  originalPwd="${PWD}"
  cp -r "projects/${PROJECT_NAME}" "${WORK_DIR}"
  cd "${WORK_DIR}/${PROJECT_NAME}"

  git init
  git add --all
  git commit -m "Initial commit"
  git remote add origin "${GERRIT_PROTOCOL}://${GERRIT_USERNAME}:${GERRIT_PASSWORD}@${GERRIT_URL}/${PROJECT_NAME}"
  git push --force

  cd "${originalPwd}"
  rm -rf "${WORK_DIR}"
}

function upload_key() {
  curl "${GERRIT_PROTOCOL}://${GERRIT_URL}/a/accounts/self/sshkeys" \
    -X POST \
    -H "Content-Type: text/plain" \
    -u "${GERRIT_USERNAME}:${GERRIT_PASSWORD}" \
    -d @${HOME}/.ssh/id_rsa.pub
}

gerrit_allow_force_update
load_project ${@}
upload_key
