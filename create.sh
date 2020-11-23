#!/usr/bin/env bash

# ./docker-manager.sh "up" "portainer"

./kubernetes-manager.sh "dashboard" "create"
# ./kubernetes-manager.sh "gerrit" "create"
# ./kubernetes-manager.sh "ghost" "create"
# ./kubernetes-manager.sh "jenkins" "create"
# ./kubernetes-manager.sh "kube-system" "create"
# ./kubernetes-manager.sh "ldap" "create"
# ./kubernetes-manager.sh "monitoring" "create"
# ./kubernetes-manager.sh "nexus" "create"
# ./kubernetes-manager.sh "pgadmin" "create"
# ./kubernetes-manager.sh "phpmyadmin" "create"
# ./kubernetes-manager.sh "plantuml" "create"
# ./kubernetes-manager.sh "redmine" "create"
# ./kubernetes-manager.sh "sonarqube" "create"
# ./kubernetes-manager.sh "xwiki" "create"

./kubernetes-manager.sh "dashboard" "token"
./kubernetes-manager.sh "ports"