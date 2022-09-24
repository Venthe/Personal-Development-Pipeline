#!/usr/bin/env bash

function _curl() {
    curl "${1}" \
        --location \
        --silent \
        --remote-name \
        "${@:2}"
}

SCHEMA_ADDRESS="https://raw.githubusercontent.com/SchemaStore/schemastore/master/src/schemas/json"

_curl "${SCHEMA_ADDRESS}/github-workflow.json"
_curl "${SCHEMA_ADDRESS}/github-action.json"
_curl "${SCHEMA_ADDRESS}/github-workflow-template-properties.json"