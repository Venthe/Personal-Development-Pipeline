#!/usr/bin/bash

. .env

function create_user() {
    ./_rest.sh login
    # ./_rest.sh rest GET a/accounts/self
}

function generate_ssh_key() {
    ssh-keygen -b 2048 -t rsa -f ${TEMP_SSHKEY} -q -N "" <<<y 2>&1 >/dev/null
    ssh-keygen -y -f ${TEMP_SSHKEY} > ${TEMP_SSHKEY}.pub
    local ssh_key_pub="$(cat ${TEMP_SSHKEY}.pub)"
    ./_rest.sh rest POST a/accounts/admin/sshkeys \
        --header 'Content-Type: text/plain' \
        --data "${ssh_key_pub}"
}

function remove_ssh_key() {
    set -x
    local ssh_key_pub=$(cat ${TEMP_SSHKEY}.pub)
    ./_rest.sh rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output
    ./_rest.sh rest DELETE a/accounts/self/sshkeys/$(./_rest.sh rest \
        GET a/accounts/self/sshkeys \
        | jq ".[] | select(.ssh_public_key | contains(\"${ssh_key_pub}\")) | .seq" \
            --raw-output \
    )
    rm "${TEMP_SSHKEY}" "${TEMP_SSHKEY}.pub"
}

"${@}"
