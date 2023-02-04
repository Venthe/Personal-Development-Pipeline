#!/usr/bin/env bash

set -o errexit
set -o pipefail

OUTPUT=/tmp/output.txt
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_SKIP_VERIFY=true

function is_running() {
  printf "$(vault status > /dev/null 2>&1 && printf "0" || printf $?)"
}

>&2 echo "Waiting for vault to start..."
while [[ "$(is_running)" -ne "0" ]]; do
  echo "waiting..."
  sleep 1
done

>&2 echo "Initialising vault..."
vault operator init -n 1 -t 1 >>${OUTPUT?}

unseal=$(cat ${OUTPUT?} | grep "Unseal Key 1:" | sed -e "s/Unseal Key 1: //g")
root=$(cat ${OUTPUT?} | grep "Initial Root Token:" | sed -e "s/Initial Root Token: //g")

>&2 echo "Unsealing..."
vault operator unseal ${unseal?}

>&2 echo "Logging in..."
vault login -no-print ${root?}

>&2 echo "Setting Policies..."
. ./policies.sh
>&2 echo "Enabling LDAP..."
. ./ldap.sh
>&2 echo "Enabling Kubernetes..."
. ./kubernetes.sh

>&2 echo "Enabling secrets..."
. ./secrets.sh

>&2 echo "Creating read only token..."
vault token create -policy=readonly-infrastructure > /tmp/read-only.token

export readonly=$(cat /tmp/read-only.token | grep 'token ' | awk '{print $2}')

echo "UNSEAL: ${unseal}
ROOT: ${root}
READONLY: ${readonly}"

#UNSEAL: WX/DMqt2RhyC2Sq7iA5lHoWIhbY/F3bYEiB3ORBwOiU=
#ROOT: hvs.oA8MpChtdN3wLQCefl1gIoc7
#READONLY: hvs.CAESIPU-JaxNLUWTaM6mBwhQaF27TT_sQV3uDwGmsWtshxCYGh4KHGh2cy45M1NGQm1ESXluU1JrN0s1alRZdWVpd0c
