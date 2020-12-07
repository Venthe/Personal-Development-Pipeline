#!/bin/env bash

function provide_project() {
    ./kubernetes/helm-apps/gerrit/provision.sh provide_project "${1}" "${2}"
}

provide_project Sample-Project ./git-projects/Sample-Project
# provide_project Jenkins ./git-projects/Jenkins
# provide_project Sample-Jenkins-Git-Repository ./git-projects/Sample-Jenkins-Git-Repository
# provide_project Helm-Gerrit ./kubernetes/helm-apps/gerrit
# provide_project Helm-Ghost ./kubernetes/helm-apps/ghost
# provide_project Helm-Jenkins ./kubernetes/helm-apps/jenkins
# provide_project Helm-Monitoring ./kubernetes/helm-apps/monitoring
# provide_project Helm-Nexus ./kubernetes/helm-apps/nexus
# provide_project Helm-PgAdmin4 ./kubernetes/helm-apps/pgadmin4
# provide_project Helm-PhpMyAdmin ./kubernetes/helm-apps/phpmyadmin
# provide_project Helm-Plantuml ./kubernetes/helm-apps/plantuml
# provide_project Helm-Redmine ./kubernetes/helm-apps/redmine
# provide_project Helm-SonarQube ./kubernetes/helm-apps/sonarqube
# provide_project Helm-Xwiki ./kubernetes/helm-apps/xwiki