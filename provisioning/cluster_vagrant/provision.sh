#!/usr/bin/bash

set -e

MACHINE_NAME=${MACHINE_NAME:-kubernetes-main-node}
PROVIDER=${PROVIDER:-hyperv}
INVENTORY=$(vagrant ssh-config ${MACHINE_NAME} | grep HostName | awk '{print $2}')

echo "Provider: ${PROVIDER}"
echo "Inventory: ${INVENTORY}"

function provision() {
    PLAYBOOK_PATH=${1}
    shift
    
    ansible-playbook ./$PLAYBOOK_PATH --key-file ./.vagrant/machines/$MACHINE_NAME/$PROVIDER/private_key --user vagrant --inventory $INVENTORY, $@
}

if [[ "$#" -ne 0 ]]; then
    provision "$@"
else
    provision ./provisioning/1.8_loadbalancer.yml
    provision ./provisioning/1.9b_externaldns-coredns/ansible.yml
    provision ./provisioning/1.10_ingress.yml
    provision ./provisioning/1.11_dashboard.yml
    provision ./provisioning/2.1_ldap.yml --extra-vars '
    {
        "namespace": "ldap",
        "admin_password": "secret",
        "ldap_domain": "example.org",
        "ldap_organisation": "My Company",
        "should_clear": false
    }
    '
fi
