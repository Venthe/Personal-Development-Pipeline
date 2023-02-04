#!/usr/bin/env bash

docker build --quiet \
             --tag venthe/alpine-git \
             .

WORK_DIR="/mnt/z/_github/Personal-Development-Pipeline/kubernetes/apps/${1}"
SSH_DIR="/mnt/c/Users/jacek/.ssh"
PROJECT_NAME="${2}"
GERRIT_URL="192.168.4.2"
GERRIT_USERNAME="jjuly"
GERRIT_BRANCH="main"
GIT_USERNAME="Jacek Lipiec"
GIT_MAIL="jacek.lipiec.bc@gmail.com"

docker run --tty \
           --interactive \
           --rm \
           --env PROJECT_NAME="${PROJECT_NAME}" \
           --env GERRIT_URL="${GERRIT_URL}" \
           --env GERRIT_USERNAME="${GERRIT_USERNAME}" \
           --env GERRIT_BRANCH="${GERRIT_BRANCH}" \
           --env GIT_USERNAME="${GIT_USERNAME}" \
           --env GIT_MAIL="${GIT_MAIL}" \
           --volume "${WORK_DIR}":"/workdir" \
           --volume "${SSH_DIR}":"/root/workdir_ssh" \
           --attach stdout \
           venthe/alpine-git
