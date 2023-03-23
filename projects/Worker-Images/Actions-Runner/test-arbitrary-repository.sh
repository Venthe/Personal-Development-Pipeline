#!/usr/bin/env bash

set -o pipefail
set -o errexit

ENVIRONMENT="${ENVIRONMENT:-k8s}"

PIPELINE_JOB_NAME="${1}" \
PIPELINE_WORKFLOW="${2}" \
ENVIRONMENT_PATH="${PWD}/test/common/${ENVIRONMENT}/env" \
EVENT_FILE="${PWD}/test/common/${ENVIRONMENT}/event.yaml" \
SECRETS_PATH="${PWD}/test/common/secrets" \
bash "./manager.sh" execute "${3}"

#PIPELINE_DEBUG=0 ENVIRONMENT=k8s bash ./test-arbitrary-repository.sh build build.yml ~/repositories/bancassurance
#PIPELINE_DEBUG=0 ENVIRONMENT=localhost bash ./test-arbitrary-repository.sh build build.yml ~/repositories/bancassurance
