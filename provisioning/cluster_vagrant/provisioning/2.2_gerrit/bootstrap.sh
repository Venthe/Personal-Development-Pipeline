#!/usr/bin/bash

set -e

. .env

. ./users.sh
. ./groups.sh
. ./projects.sh

create_user

set_ldap_subgroup "Administrators" "Administrators"
create_group "Integrators" "Administrators" "contains all integrators"
set_ldap_subgroup "Integrators" "Integrators"
create_group "Developers" "Integrators" "contains all developers"
set_ldap_subgroup "Developers" "Developers"
set_ldap_subgroup "Service%20Users" "Non-Interactive%20Users"

create_label "Verified" '{
    "commit_message": "Create verified label",
    "values": {
      " 0": "No score",
      "-1": "I would prefer this is not merged as is",
      "-2": "This shall not be merged",
      "+1": "Looks good to me, but someone else must approve",
      "+2": "Looks good to me, approved"
    }
  }'

generate_ssh_key

provide_project "Jenkins" "${GERRIT_ROOT_PWD}/git-projects/Jenkins" "${GERRIT_TEMP_SSHKEY}"
provide_project "Sample-Jenkins-Git-Repository" "${GERRIT_ROOT_PWD}/git-projects/Sample-Jenkins-Git-Repository" "${GERRIT_TEMP_SSHKEY}"
provide_project "Sample-Project" "${GERRIT_ROOT_PWD}/git-projects/Sample-Project" "${GERRIT_TEMP_SSHKEY}"
provide_project "Cluster-Vagrant" "${GERRIT_ROOT_PWD}/provisioning/cluster_vagrant" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-Ghost" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/ghost" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-Monitoring" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/monitoring" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-PgAdmin4" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/pgadmin4" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-PhpMyAdmin" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/phpmyadmin" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-Plantuml" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/plantuml" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-Redmine" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/redmine" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-SonarQube" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/sonarqube" "${GERRIT_TEMP_SSHKEY}"
provide_project "Helm-Xwiki" "${GERRIT_ROOT_PWD}/kubernetes/helm-apps/xwiki" "${GERRIT_TEMP_SSHKEY}"

remove_ssh_key
