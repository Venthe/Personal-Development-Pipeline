#!/usr/bin/env bash

./docker-manager.sh "clean" "portainer"

./kubernetes-manager.sh "dashboard" "wipe"
./kubernetes-manager.sh "gerrit" "wipe"
./kubernetes-manager.sh "ghost" "wipe"
./kubernetes-manager.sh "jenkins" "wipe"
./kubernetes-manager.sh "kube-system" "wipe"
./kubernetes-manager.sh "ldap" "wipe"
./kubernetes-manager.sh "monitoring" "wipe"
./kubernetes-manager.sh "nexus" "wipe"
./kubernetes-manager.sh "pgadmin" "wipe"
./kubernetes-manager.sh "phpmyadmin" "wipe"
./kubernetes-manager.sh "plantuml" "wipe"
./kubernetes-manager.sh "redmine" "wipe"
./kubernetes-manager.sh "sonarqube" "wipe"
./kubernetes-manager.sh "xwiki" "wipe"