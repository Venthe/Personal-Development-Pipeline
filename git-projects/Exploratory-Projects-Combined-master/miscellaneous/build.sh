#!/usr/bin/env bash

. ./.env
. ./gradle.properties

COMMIT_HASH=$(git log -1 --pretty=%H)
APP_VERSION="${version}"
TAG="${APP_VERSION}.${COMMIT_HASH}"

echo "Building image. COMMIT_HASH=${COMMIT_HASH},APP_VERSION=${APP_VERSION},TAG=${TAG}"
docker build --build-arg="build=${TAG}" \
             --build-arg="appName=${APP_NAME}" \
             --tag "${APP_NAME}:${TAG}" \
             .

function get_data() {
  local LABEL_IMAGE=$1
  docker image ls --all \
          --filter "label=build=${TAG}" \
          --filter "label=appName=${APP_NAME}" \
          --filter "label=image=${LABEL_IMAGE}" \
          --no-trunc \
          --format "{{.ID}}" \
          | sort -u
}

#docker build . --progress=plain

#
#function clean_up() {
#    echo "Cleaning up"
#    get_data 'clean-install' | xargs docker rm
#    get_data 'package'| xargs docker rm
#}
#
#### TESTS ###
#TEST_IMAGE="$(get_data 'clean-install')"
#
#echo "Running unit tests for ${TEST_IMAGE}"
#docker run "${TEST_IMAGE}" mvn test
#
#echo "Running integration tests for ${TEST_IMAGE}"
#docker run --network=kafka_default "${TEST_IMAGE}" mvn test \
#         -Dspring.active.profiles=integrationTest \
#         -Dspring.data.mongodb.host=mongo \
#         -Dspring.data.kafka.bootstrap-server=kafka

clean_up
