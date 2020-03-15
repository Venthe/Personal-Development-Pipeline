#!/usr/bin/env bash

bash ./docker-manager.sh recreate "portainer"
bash ./kubernetes-manager.sh "dashboard" "recreate"
bash ./kubernetes-manager.sh "metrics" "recreate"
bash ./kubernetes-manager.sh "nexus" "recreate"
bash ./kubernetes-manager.sh "gerrit" "recreate"
bash ./kubernetes-manager.sh "sonarqube" "recreate"
bash ./kubernetes-manager.sh "jenkins" "recreate"
bash ./kubernetes-manager.sh "pgadmin" "recreate"
bash ./kubernetes-manager.sh "dashboard" "token"
bash ./kubernetes-manager.sh "ports"