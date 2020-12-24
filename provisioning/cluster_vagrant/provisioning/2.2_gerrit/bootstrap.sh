#!/usr/bin/bash

. .env

./users.sh create_user

./groups.sh set_ldap_subgroup "Administrators" "Administrators"
./groups.sh create_group "Integrators" "Administrators" "contains all integrators"
./groups.sh set_ldap_subgroup "Integrators" "Integrators"
./groups.sh create_group "Developers" "Integrators" "contains all developers"
./groups.sh set_ldap_subgroup "Developers" "Developers"
./groups.sh set_ldap_subgroup "Service%20Users" "Non-Interactive%20Users"

./projects.sh "Verified" '{
    "commit_message": "Create verified label",
    "values": {
      " 0": "No score",
      "-1": "I would prefer this is not merged as is",
      "-2": "This shall not be merged",
      "+1": "Looks good to me, but someone else must approve",
      "+2": "Looks good to me, approved"
    }
  }'

./users.sh generate_ssh_key

./projects.sh provide_project "Jenkins" "${ROOT_PWD}/git-projects/Jenkins" "${TEMP_SSHKEY}"
./projects.sh provide_project "Sample-Jenkins-Git-Repository" "${ROOT_PWD}/git-projects/Sample-Jenkins-Git-Repository" "${TEMP_SSHKEY}"
./projects.sh provide_project "Sample-Project" "${ROOT_PWD}/git-projects/Sample-Project" "${TEMP_SSHKEY}"
./projects.sh provide_project "Cluster-Vagrant" "${ROOT_PWD}/provisioning/cluster_vagrant" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Ghost" "${ROOT_PWD}/kubernetes/helm-apps/ghost" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Jenkins" "${ROOT_PWD}/kubernetes/helm-apps/jenkins" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Monitoring" "${ROOT_PWD}/kubernetes/helm-apps/monitoring" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Nexus" "${ROOT_PWD}/kubernetes/helm-apps/nexus" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-PgAdmin4" "${ROOT_PWD}/kubernetes/helm-apps/pgadmin4" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-PhpMyAdmin" "${ROOT_PWD}/kubernetes/helm-apps/phpmyadmin" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Plantuml" "${ROOT_PWD}/kubernetes/helm-apps/plantuml" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Redmine" "${ROOT_PWD}/kubernetes/helm-apps/redmine" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-SonarQube" "${ROOT_PWD}/kubernetes/helm-apps/sonarqube" "${TEMP_SSHKEY}"
./projects.sh provide_project "Helm-Xwiki" "${ROOT_PWD}/kubernetes/helm-apps/xwiki" "${TEMP_SSHKEY}"

./users.sh remove_ssh_key
