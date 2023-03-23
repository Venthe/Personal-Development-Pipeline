#!/usr/bin/env bash

set -e

. ./.env
. ./manager.sh

# GET_documentation | yq --prettyPrint > documentation.yaml

# POST_blobstores_file apt-hosted
# POST_blobstores_file apt-proxy

# docker run -it --rm ubuntu:20.10 cat /etc/apt/sources.list 2>&1 | grep -Ev "^#" | sort | uniq -u


function add_apt_proxy_repository() {
    local type="${1}"
    local url="${2}"
    local distribution="${3}"
    local component="${@:4}"

    echo "${component}"
}

# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy main restricted
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy multiverse
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy universe
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy-backports main restricted universe multiverse
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy-updates main restricted
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy-updates multiverse
# add_apt_proxy_repository deb http://archive.ubuntu.com/ubuntu/ groovy-updates universe
# add_apt_proxy_repository deb http://security.ubuntu.com/ubuntu/ groovy-security main restricted
# add_apt_proxy_repository deb http://security.ubuntu.com/ubuntu/ groovy-security multiverse
# add_apt_proxy_repository deb http://security.ubuntu.com/ubuntu/ groovy-security universe