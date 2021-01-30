#!/bin/env bash

DEFAULT_CONTAINER_NAME=${DEFAULT_CONTAINER_NAME:=nexus}
NEXUS_USERNAME=admin
NEXUS_PASSWORD="${NEXUS_PASSWORD:=admin123}"
NEXUS_URL="${BASE_URL:=localhost:8081}/service/rest"

function call() {
    local METHOD=$1
    >&2 echo "* Calling with ${method} ${@:2}"
    curl --user "${NEXUS_USERNAME}:${NEXUS_PASSWORD}" \
         --request "${METHOD}" \
         --show-error \
         --fail \
         --silent \
         --insecure \
         "${NEXUS_URL}${@:2}"
}

function GET_documentation() {
    call GET /swagger.json $@
}

function PUT_user_password() {
    local USER_ID=$1
    local NEW_PASSWORD=$2

    call PUT "/v1/security/users/${USER_ID}/change-password" \
         --data "${NEW_PASSWORD}" \
         --header 'content-type: text/plain' \
         "${@:3}"
}

function GET_blobstores() {
    call GET /v1/blobstores \
         "${@}"
}

function POST_blobstores_file() {
    local NAME=$1
    call POST /v1/blobstores/file \
         --data "{\"name\":\"${NAME}\", \"path\":\"${NAME}\"}" \
         --header 'content-type: application/json' \
         "${@:2}"
}

function GET_repositories() {
    call GET /v1/repositories \
         "${@}"
}

function POST_repositories() {
    local REPOSITORY_TYPE=$1
    local DATA=$2
    call POST /v1/repositories/${REPOSITORY_TYPE} \
         --data ${DATA} \
         --header "content-type: application/json" \
         "${@:3}"
}

function GET_anonymous_access() {
    call GET "/v1/security/anonymous" \
         "${@}"
}

function PUT_anonymous_access() {
    local ENABLED=${1:-true}
    local USER_ID=${2:-anonymous}
    local REALM_NAME=${3:-NexusAuthorizingRealm}
    call PUT "/v1/security/anonymous" \
         --data "{\"enabled\":${ENABLED},\"userId\":\"${USER_ID}\",\"realmName\":\"${REALM_NAME}\"}" \
         --header 'content-type: application/json' \
         "${@:4}"
}

function run_in_docker() {
    local CONTAINER_NAME="${1:-$DEFAULT_CONTAINER_NAME}"
    docker run \
           --detach \
           --publish 8081:8081 \
           --publish 5000:5000 \
           --publish 5001:5001 \
           --name "${CONTAINER_NAME}" \
           sonatype/nexus3 \
           "${@:2}"
}

function PUT_security_realms_active() {
    call PUT /v1/security/realms/active \
         --header 'content-type: application/json' \
         --data "${1}" \
         ${@:2}
}

function wait_for_nexus() {
    until [[ $(call GET /v1/blobstores --output /dev/null --silent --head --fail; echo $?) == 0 ]]; do
        printf '.'
        sleep 5
    done
    printf '\n'
}

if [[ ${#} -ne 0 ]]; then
    if declare -f "$1" > /dev/null
    then
        # call arguments verbatim
        "$@"
    else
        # Show a helpful error
        echo "'$1' is not a known function name" >&2
        exit 1
    fi
fi