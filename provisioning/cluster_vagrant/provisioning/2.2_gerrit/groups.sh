#!/usr/bin/bash

set -e

. ./_rest.sh

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

if [[ ${#} -ne 0 ]]; then
    "${@}"
fi
