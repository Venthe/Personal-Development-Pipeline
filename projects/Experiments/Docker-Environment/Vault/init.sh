#!/usr/bin/env sh

function is_running() {
  printf "$(docker-entrypoint.sh status > /dev/null 2>&1 && printf "0" || printf $?)"
}

set -e

>&2 echo "Starting vault server..."
docker-entrypoint.sh server "$@" &

>&2 echo "Waiting for vault to start..."
while [[ "$(is_running)" -ne "0" ]]; do
  echo "waiting..."
  sleep 1
done

>&2 echo "Adding secrets..."
>&2 echo "  Logging to vault"
docker-entrypoint.sh login "$VAULT_DEV_ROOT_TOKEN_ID"
cd /mnt/init/secrets

>&2 echo "  Loading JSON secrets"
find . -type f -name '*.json' \
    | xargs -I{} docker-entrypoint.sh kv put -format=json "secret/${APPLICATION_NAME}" "@{}"

wait
