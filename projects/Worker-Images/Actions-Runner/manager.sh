#!/usr/bin/env bash

set -o errexit
set -o pipefail

#Links (Local)
#_GERRIT_URL="ssh://admin@host.docker.internal:29418"
#_NEXUS_URL="http://host.docker.internal:8081/repository/raw"
#_DOCKER_URL="http://host.docker.internal:5000"
#_DOCKER_TAG="host.docker.internal:5000"

NAMESPACE="infrastructure"
LATEST_TAG="latest"

CURRENT_VERSION_TAG=`git rev-parse HEAD`
CURRENT_DATETIME=`date --utc +%Y%m%d-%H%M%S`
CURRENT_BRANCH=`git branch --show`

THIS_TAG="${CURRENT_BRANCH}-${CURRENT_DATETIME}-${CURRENT_VERSION_TAG}"

#Links (Remote)
_GERRIT_URL="ssh://admin@ssh.gerrit.home.arpa:29418"
_DOCKER_URL="https://docker.home.arpa"
_NEXUS_URL="https://nexus.home.arpa/repository/raw-hosted"
_DOCKER_TAG="docker.home.arpa"

# Docker image details
RUNNER_BASE_IMAGE="docker.io/library/ubuntu:22.10"
NODE_VERSION=19

# Other
RUNNER_IMAGE="${_DOCKER_TAG}/venthe/ubuntu-runner:22.10"

function cleanPipeline() {
  rm -rf ./node_modules/@pipeline
}

function buildActions() {
  (cd ./actions && bash ./manager.sh)
}

function buildAction() {
  (cd ./actions && bash ./manager.sh buildSingle "${1}")
}

function buildLibraries() {
  (cd ./libraries && bash ./manager.sh)
  cleanPipeline
  npm install
}

function buildLibrary() {
  (cd ./libraries && bash ./manager.sh buildSingle "${1}")
  cleanPipeline
  npm install
}

function prepare() {
  if [[ "${REBUILD_LIBRARIES}" -eq "1" ]]; then
    buildLibraries
  fi
  npm install
}

function buildManager() {
  npm install
  npm run build
}

function buildContainer() {
  DOCKER_BUILDKIT=0 docker build \
    --progress=plain \
    --tag="${RUNNER_IMAGE}" \
    --file=Dockerfile \
    ./
}

# REBUILD_MANAGER=1 ./manager.sh test tests/remote-actions
function execute() {
  local repositoryPath="${1}"
  local envPath="${ENVIRONMENT_PATH:-${PWD}/env/}"
  local eventFile="${EVENT_FILE:-${PWD}/event.yaml}"
  local secretsPath="${SECRETS_PATH:-${PWD}/secrets/}"

  function project() {
    if [[ "${ENVIRONMENT}" != "localhost" ]]; then
      (cd ../../../ && bash ./load-projects.sh "${@}" Test/ActionRunnerTestService)
    else
      function loadProject() {
        docker run --rm -it \
            --volume="${HOME}/.gitconfig:/root/.gitconfig:ro" \
             -p "8080:80" \
            --name="git-server" \
            --detach \
            docker.home.arpa/venthe/git-server:latest
        sleep 2

        local temp_directory="$(mktemp -d)"
        trap 'rm -rf -- "${temp_directory}"' EXIT INT
        rsync --recursive --exclude="node_modules" --exclude=".git" "${repositoryPath}" "${temp_directory}"
        (cd ${temp_directory} \
          && git init \
          && git add --all 2>/dev/null \
          && git commit -m "Initial commit" \
          && git push http://localhost:8080/repository.git main)
      }

      function deleteProject() {
        docker stop git-server
      }

      ${@}
    fi
  }

  project deleteProject || true
  project loadProject "${repositoryPath}"

  docker run \
    --rm --interactive --tty \
    --volume "${HOME}/.kube/config:/root/.kube_test/config:ro" \
    --volume "${HOME}/.ssh/:/root/.ssh_test:ro" \
    --volume "${PWD}/dist/index.js:/runner/index.js" \
    --volume "${PWD}/dist/sourcemap-register.js:/runner/sourcemap-register.js" \
    --volume "${PWD}/dist/index.js.map:/runner/index.js.map" \
    --volume "${envPath}:/runner/metadata/env:ro" \
    --volume "${eventFile}:/runner/metadata/event.yaml:ro" \
    --volume "${secretsPath}:/runner/metadata/secrets:ro" \
    --volume "${PWD}/test/test.sh:/test.sh" \
    --volume "/etc/ssl/certs/ca-certificates.crt:/etc/ssl/certs/ca-certificates.crt:ro" \
    --volume "/usr/local/share/ca-certificates/k8s/ca.crt:/certs/ca.crt:ro" \
    \
    --privileged \
    \
    --env PIPELINE_JOB_NAME="${PIPELINE_JOB_NAME:-TestedJob}" \
    --env PIPELINE_BUILD_ID="1" \
    --env PIPELINE_DEBUG="${PIPELINE_DEBUG:-1}" \
    --env PIPELINE_WORKFLOW="${PIPELINE_WORKFLOW:-workflow.yaml}" \
    --env PIPELINE_NEXUS_URL="${_NEXUS_URL}" \
    --env PIPELINE_GERRIT_URL="${_GERRIT_URL}" \
    --env PIPELINE_DOCKER_URL="${_DOCKER_URL}" \
    \
    --env GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no" \
    "${RUNNER_IMAGE}" \
    "${2:-/test.sh}"

  project deleteProject
}

function build() {
  buildManager && buildContainer
}

function publishContainer() {
  docker login docker.home.arpa
  docker push "${RUNNER_IMAGE}"
}

function full() {
  build
  publishContainer
}

prepareNPM() {
  npm set strict-ssl=false
  npm adduser --auth-type=legacy --registry https://nexus.home.arpa/repository/npm-hosted/
}

function test_all() {
  npm run test ${@} --prefix=test
}

if [[ "${REBUILD_LIBRARIES}" -eq "1" ]]; then
  buildLibraries
fi

if [[ "${REBUILD_MANAGER}" -eq "1" ]]; then
  buildManager
fi

if [[ "${REBUILD_CONTAINER}" -eq "1" ]]; then
  buildContainer
fi

if [[ "${REBUILD_ACTIONS}" -eq "1" ]]; then
  buildActions
fi

if [[ ${#} -ne 0 ]]; then
  if declare -f "$1" >/dev/null; then
    # call arguments verbatim
    "${@}"
  else
    # Show a helpful error
    echo >&2 "'$1' is not a known function name"
    exit 1
  fi
fi
