#!/usr/bin/env bash

set -o pipefail
set -o errexit

REPOSITORY_PATH="${1}" \
PIPELINE_JOB_NAME="${2}" \
PIPELINE_WORKFLOW="${3}" \
ENVIRONMENT_PATH="${PWD}/test/common/env" \
EVENT_FILE="${PWD}/test/common/event.yaml" \
SECRETS_PATH="${PWD}/test/common/secrets" \
bash ./manager.sh test
