#!/usr/bin/bash

set -e

PROVIDER=${2:-hyperv}

echo "Provider: ${PROVIDER}"

# ansible-playbook prepare-localhost.yml
vagrant destroy --force
vagrant up --provider="${PROVIDER}"
./provision.sh "${PROVIDER}"