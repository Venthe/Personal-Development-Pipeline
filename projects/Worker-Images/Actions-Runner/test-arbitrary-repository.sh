#!/usr/bin/env bash

set -o pipefail
set -o errexit

ENVIRONMENT="${ENVIRONMENT:-k8s}"

REPOSITORY_PATH="${3}" \
PIPELINE_JOB_NAME="${1}" \
PIPELINE_WORKFLOW="${2}" \
ENVIRONMENT_PATH="${PWD}/test/common/${ENVIRONMENT}/env" \
EVENT_FILE="${PWD}/test/common/${ENVIRONMENT}/event.yaml" \
SECRETS_PATH="${PWD}/test/common/${ENVIRONMENT}/secrets" \
bash "./manager.sh" test

#./manager.sh buildAction checkout && EXPERIMENTAL_LOADER=1 PIPELINE_DEBUG=0 REBUILD_CONTAINER=1 REBUILD_MANAGER=1 ENVIRONMENT=localhost bash ./test-arbitrary-repository.sh build build.yml
