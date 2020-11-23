#!/usr/bin/bash

PROVIDER=${1:-hyperv}

ansible-playbook prepare-localhost.yml
vagrant destroy --force
vagrant up --provider="${PROVIDER}"
