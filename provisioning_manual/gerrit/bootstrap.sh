#!/usr/bin/bash

set -e

GERRIT_USER=${GERRIT_USER:-admin}
GERRIT_PASSWORD=${GERRIT_PASSWORD:-secret}
GERRIT_URL=${GERRIT_URL:-https://gerrit.home.arpa}
GERRIT_NAMESPACE=${GERRIT_NAMESPACE:-gerrit}

function rest() {
    local basic_auth=$(printf ${GERRIT_USER}:${GERRIT_PASSWORD} | base64)
    local method="${1}"
    local path="${2}"
    shift 2
    local url="${GERRIT_URL}/${path}"
    >&2 echo "* ${method} '${url}'"
    curl --request "${method}" \
         --show-error \
         --fail \
         --silent \
         --header "Authorization: Basic ${basic_auth}" \
         "${url}" "${@}" \
         | sed '0,/)]}/ d'  | tee /dev/fd/2
}

function login() {
    rest POST "login/" \
        --header 'Content-Type: application:x-www-form-urlencoded' \
        --data "username=${GERRIT_USER}&password=${GERRIT_PASSWORD}"
}


function query_existing_group_id() {
   rest GET "a/groups/?query=inname:${1}" \
    --header "Accept: application/json" \
    | jq '.[0].id' --raw-output
}

function get_ldap_subgroup_id() {
  rest GET "a/groups/?s=ldap/${1}" \
    --header "Accept: application/json" \
    | jq 'map(select(.)) | first .id' --raw-output
}

function set_ldap_subgroup() {
    local group_name="${1}"
    local subgroup_name="${2}"
    local subgroup_id=$(get_ldap_subgroup_id ${subgroup_name})
    rest PUT "a/groups/${group_name}/groups/${subgroup_id}" \
        --header "Accept: application/json"
}

function create_group() {
    local group_name="${1}"
    local owner="${2}"
    local description="${3}"
    rest PUT "a/groups/${group_name}" \
        --header "Content-Type: application/json" \
        --header "Accept: application/json" \
        --data '{
            "description": "'"${description}"'",
            "visible_to_all": true,
            "owner": "'"${owner}"'",
            "owner_id": "'"$(query_existing_group_id Administrators)"'"
        }'
}

function create_label() {
    local label_name="${1}"
    local data="${2}"
    rest PUT "a/projects/All-Projects/labels/${label_name}" \
    --header "Content-Type: application/json" \
    --data "${data}"
}

function login() {
    rest POST "login/" \
        --header 'Content-Type: application:x-www-form-urlencoded' \
        --data "username=${GERRIT_USER}&password=${GERRIT_PASSWORD}"
}

function create_user() {
    echo "* Creating user ${GERRIT_USER}"
    login
}

login

set_ldap_subgroup "Administrators" "Administrators"
create_group "Integrators" "Administrators" "contains all integrators"
set_ldap_subgroup "Integrators" "Integrators"
create_group "Developers" "Integrators" "contains all developers"
set_ldap_subgroup "Developers" "Developers"
set_ldap_subgroup "Service%20Users" "Non-Interactive%20Users"

create_label "Verified" '{
    "commit_message": "Create verified label",
    "function": "AnyWithBlock",
    "copy_all_scores_if_no_change": true,
    "values": {
      " 0": "No score",
      "-1": "I would prefer this is not merged as is",
      "+1": "Looks good to me, but someone else must approve"
    }
  }'
