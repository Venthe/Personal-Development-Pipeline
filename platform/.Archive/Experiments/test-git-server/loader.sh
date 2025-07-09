#!/usr/bin/env bash

set -o pipefail
set -o errexit
set -o xtrace

TAG=docker.home.arpa/venthe/git-server:latest

function build_container() {
  docker build ./ --file="Dockerfile" --tag="${TAG}"
}

function join() {
  docker exec -it git-server bash
}

function start_container() {
  local project_directory="${1}"
#    --detach \
  docker run --rm -it \
    --volume="${PWD}/loader.sh:/usr/bin/loader.sh:ro" \
    --volume="${PWD}/git.conf:/etc/nginx/conf.d/default.conf:ro" \
    --volume="${PWD}/nginx.conf:/etc/nginx/nginx.conf:ro" \
    --volume="${HOME}/.gitconfig:/root/.gitconfig:ro" \
    --volume="${project_directory}:/original_project:ro" \
     -p "8080:80" \
    --name="git-server" \
    "${TAG}"
}

function update_repo() {
  rm -rf /test_project/*
  prepare_original_repo
  cd "/test_project"
  git push --force /usr/share/nginx/html/repository.git main
}

function prepare_original_repo() {
  mkdir "/test_project" || true
  rsync --recursive --partial --info=progress2 --exclude="node_modules" -exclude="target" --exclude=".git" "/original_project/" "/test_project/"
  cd "/test_project"
  git init
  git commit --allow-empty --message "Initial commit"
  git add --all 1>/dev/null 2>/dev/null
  git commit --message "Add initial code" 1>/dev/null 2>/dev/null || true
}

function server() {
  cd "/usr/share/nginx/html/"
  mkdir "repository.git"
  cd "repository.git"
  git init . --bare --shared
  git update-server-info
  echo "
[core]
  repositoryformatversion = 0
  filemode = true
  bare = true
  sharedrepository = 1
[receive]
  denyNonFastforwards = false
[http]
  receivepack = true" > /usr/share/nginx/html/repository.git/config
  spawn-fcgi -s /var/run/fcgiwrap.socket -M 766 /usr/sbin/fcgiwrap
  nginx ${@}
}

${@}
