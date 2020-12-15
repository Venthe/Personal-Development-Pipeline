#!/usr/bin/bash

set -e

PROVIDER=${2:-hyperv}

echo "Provider: ${PROVIDER}"

# ansible-playbook prepare-localhost.yml
vagrant destroy --force
VAGRANT_EXPERIMENTAL="disks" vagrant up --provider="${PROVIDER}"
./provision.sh "${PROVIDER}"