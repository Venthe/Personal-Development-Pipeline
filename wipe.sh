#!/usr/bin/env bash

bash ./docker-manager.sh "clean" "portainer"
bash ./kubernetes-manager.sh "dashboard" "wipe"
bash ./kubernetes-manager.sh "metrics" "wipe"
bash ./kubernetes-manager.sh "nexus" "wipe"
bash ./kubernetes-manager.sh "gerrit" "wipe"
bash ./kubernetes-manager.sh "sonarqube" "wipe"
bash ./kubernetes-manager.sh "jenkins" "wipe"
bash ./kubernetes-manager.sh "pgadmin" "wipe"
bash ./kubernetes-manager.sh "xwiki" "wipe"
bash ./kubernetes-manager.sh "ghost" "wipe"