#!/usr/bin/bash

set -e

. ./_rest.sh

function create_user() {
    echo "* Creating user ${GERRIT_USER}"
    login
}

function create_ssh_key() {
    ssh-keygen -b 2048 -t rsa -f ${GERRIT_TEMP_SSHKEY} -q -N "" <<<y 2>&1 >/dev/null
    ssh-keygen -y -f ${GERRIT_TEMP_SSHKEY} > ${GERRIT_TEMP_SSHKEY}.pub
}

function cat_key() {
    local pub=$1
    cat ${GERRIT_TEMP_SSHKEY}${pub}
}

function generate_ssh_key() {
    create_ssh_key
    local ssh_key_pub="$(cat_key .pub)"
    rest POST a/accounts/self/sshkeys \
        --header 'Content-Type: text/plain' \
        --data "${ssh_key_pub}"
}

function rm_ssh_key() {
    rm "${GERRIT_TEMP_SSHKEY}" "${GERRIT_TEMP_SSHKEY}.pub"
}

function remove_ssh_key() {
    local ssh_key_pub=$(cat_key .pub)
    rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output
    rest DELETE a/accounts/self/sshkeys/$(rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output \
    )
    rm_ssh_key
}

if [[ ${#} -ne 0 ]]; then
    "${@}"
fi
