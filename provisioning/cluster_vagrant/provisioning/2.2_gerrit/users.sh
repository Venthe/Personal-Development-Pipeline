#!/usr/bin/bash

DIRECTORY=${DIRECTORY:-.}

. ${DIRECTORY}/.env || true

function create_user() {
    ${DIRECTORY}/_rest.sh login
    # ./_rest.sh rest GET a/accounts/self
}

function create_ssh_key() {
    ssh-keygen -b 2048 -t rsa -f ${TEMP_SSHKEY} -q -N "" <<<y 2>&1 >/dev/null
    ssh-keygen -y -f ${TEMP_SSHKEY} > ${TEMP_SSHKEY}.pub
}

function cat_key() {
    local pub=$1
    cat ${TEMP_SSHKEY}${pub}
}

function generate_ssh_key() {
    create_ssh_key
    local ssh_key_pub="$(cat_key .pub)"
    ${DIRECTORY}/_rest.sh rest POST a/accounts/self/sshkeys \
        --header 'Content-Type: text/plain' \
        --data "${ssh_key_pub}"
}

function rm_ssh_key() {
    rm "${TEMP_SSHKEY}" "${TEMP_SSHKEY}.pub"
}

function remove_ssh_key() {
    set -x
    local ssh_key_pub=$(cat_key .pub)
    ${DIRECTORY}/_rest.sh rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output
    ${DIRECTORY}/_rest.sh rest DELETE a/accounts/self/sshkeys/$(./_rest.sh rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output \
    )
    rm_ssh_key
}

"${@}"
