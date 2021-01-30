#!/usr/bin/env bash

set -e

PROVIDER=${2:-hyperv}

echo "Provider: ${PROVIDER}"

# ansible-playbook prepare-localhost.yml
vagrant destroy --force
VAGRANT_EXPERIMENTAL="disks" vagrant up --provider="${PROVIDER}"

./provision.sh ./provisioning/1.8_loadbalancer.yml
./provision.sh ./provisioning/1.9b_externaldns-coredns/ansible.yml
./provision.sh ./provisioning/1.10_ingress.yml
./provision.sh ./provisioning/1.11_dashboard.yml
./provision.sh ./provisioning/1.12_cert-manager/ansible.yml
./provision.sh ./provisioning/2.1_ldap/ansible.yml
./provision.sh ./provisioning/2.2_gerrit/ansible.yml
./provision.sh ./provisioning/2.3_jenkins/ansible.yml
./provision.sh ./provisioning/2.4_nexus/ansible.yml
