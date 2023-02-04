#!/usr/bin/env bash

set -e

. ./.env
. ./manager.sh

# GET_documentation | yq --prettyPrint > documentation.yaml

PUT_security_realms_active @manifests/security-active-realms.json
PUT_anonymous_access true

roles=`GET_roles | jq -r '.[]|select(.id | startswith("5")) | .id'`

for role in $roles; do
  DELETE_roles $role || true
done

# TODO: More granular authority
POST_roles @manifests/security-roles-administrators.json
POST_roles @manifests/security-roles-non-interactive-users.json
POST_roles @manifests/security-roles-docker-push.json

DELETE_ldap Ldap || true

POST_ldap @manifests/ldap-configuration-home.arpa.json

# GET_repositorySettings | jq

# Delete defaults
DELETE_repositories "nuget.org-proxy" || true
DELETE_repositories "nuget-hosted" || true
DELETE_repositories "nuget-group" || true
DELETE_repositories "maven-releases" || true
DELETE_repositories "maven-public" || true
DELETE_repositories "maven-central" || true
DELETE_repositories "maven-snapshots" || true

DELETE_blobstores default || true

# Docker
DELETE_repositories docker-group || true
DELETE_repositories docker-hosted || true
DELETE_repositories docker-proxy-docker.io || true
DELETE_repositories docker-proxy-quay.io || true
DELETE_repositories docker-proxy-k8s.gcr.io || true
DELETE_blobstores docker-hosted || true
DELETE_blobstores docker-default || true
POST_blobstores_file docker-hosted
POST_blobstores_file docker-default
POST_repositories docker/hosted @manifests/repository-docker-hosted.json
POST_repositories docker/proxy @manifests/repository-docker-proxy-docker.io.json
POST_repositories docker/proxy @manifests/repository-docker-proxy-quay.io.json
POST_repositories docker/proxy @manifests/repository-docker-proxy-k8s.gcr.io.json
POST_repositories docker/group @manifests/repository-docker-group.json

# Maven
repositories_to_delete=(
  maven-group
  maven-hosted-snapshots
  maven-hosted-releases
  maven-proxy-maven.org
  maven-proxy-spring-release
  maven-proxy-spring-snapshot
  maven-proxy-spring-milestone
  maven-proxy-spring-release-2
  maven-proxy-spring-snapshot-2
  maven-proxy-spring-milestone-2
  maven-proxy-jrs-ce-releases
  maven-proxy-jaspersoft-clients-releases
)
for i in ${!repositories_to_delete[@]}; do
  DELETE_repositories ${repositories_to_delete[$i]} || true
done
DELETE_blobstores maven-default || true
DELETE_blobstores maven-hosted || true
POST_blobstores_file maven-default
POST_blobstores_file maven-hosted
POST_repositories maven/hosted @manifests/repository-maven-hosted-releases.json
POST_repositories maven/hosted @manifests/repository-maven-hosted-snapshots.json
POST_repository_maven_proxy maven.org https://repo1.maven.org/maven2/
POST_repository_maven_proxy spring-release https://repo.spring.io/release
POST_repository_maven_proxy spring-snapshot https://repo.spring.io/snapshot
POST_repository_maven_proxy spring-milestone https://repo.spring.io/milestone
POST_repository_maven_proxy spring-release-2 http://maven.springframework.org/release
POST_repository_maven_proxy spring-snapshot-2 http://maven.springframework.org/snapshot
POST_repository_maven_proxy spring-milestone-2 http://maven.springframework.org/milestone
POST_repository_maven_proxy jrs-ce-releases https://jaspersoft.jfrog.io/artifactory/jrs-ce-releases
POST_repository_maven_proxy jaspersoft-clients-releases https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-clients-releases
POST_repositories maven/group @manifests/repository-maven-group.json

# NUGET
DELETE_repositories nuget-proxy-nuget.org || true
DELETE_repositories nuget-hosted || true
DELETE_repositories nuget-group || true
DELETE_blobstores nuget-default || true
DELETE_blobstores nuget-hosted || true
POST_blobstores_file nuget-default
POST_blobstores_file nuget-hosted
POST_repositories nuget/hosted @manifests/repository-nuget-hosted.json
POST_repositories nuget/proxy @manifests/repository-nuget-proxy-nuget.org.json
POST_repositories nuget/group @manifests/repository-nuget-group.json

# Helm
DELETE_repositories helm-proxy-bitnami || true
DELETE_repositories helm-proxy-runix || true
DELETE_repositories helm-proxy-gitlab || true
DELETE_repositories helm-proxy-oteemo || true
DELETE_repositories helm-proxy-rook-release || true
DELETE_repositories helm-proxy-democratic-csi || true
DELETE_repositories helm-proxy-kubernetes-ingress-nginx || true
DELETE_repositories helm-proxy-nginx-stable || true
DELETE_repositories helm-proxy-kubernetes-dashboard || true
DELETE_repositories helm-proxy-coredns || true
DELETE_repositories helm-proxy-jetstack || true
DELETE_repositories helm-proxy-jenkins || true
DELETE_repositories helm-proxy-sonatype || true
DELETE_repositories helm-proxy-grafana || true
DELETE_repositories helm-proxy-loki || true
DELETE_repositories helm-proxy-prometheus-community || true
DELETE_repositories helm-proxy-stable || true
DELETE_repositories helm-proxy-cetic || true
DELETE_blobstores helm-default || true
POST_blobstores_file helm-default
POST_repository_helm_proxy bitnami https://charts.bitnami.com/bitnami
POST_repository_helm_proxy runix https://helm.runix.net
POST_repository_helm_proxy gitlab https://charts.gitlab.io
POST_repository_helm_proxy oteemo https://oteemo.github.io/charts
POST_repository_helm_proxy rook-release https://charts.rook.io/release
POST_repository_helm_proxy democratic-csi https://democratic-csi.github.io/charts
POST_repository_helm_proxy kubernetes-ingress-nginx https://kubernetes.github.io/ingress-nginx
POST_repository_helm_proxy nginx-stable https://helm.nginx.com/stable
POST_repository_helm_proxy kubernetes-dashboard https://kubernetes.github.io/dashboard
POST_repository_helm_proxy coredns https://coredns.github.io/helm
POST_repository_helm_proxy jetstack https://charts.jetstack.io/
POST_repository_helm_proxy jenkins https://charts.jenkins.io/
POST_repository_helm_proxy sonatype https://sonatype.github.io/helm3-charts/
POST_repository_helm_proxy grafana https://grafana.github.io/helm-charts
POST_repository_helm_proxy loki https://grafana.github.io/loki/charts
POST_repository_helm_proxy prometheus-community https://prometheus-community.github.io/helm-charts
POST_repository_helm_proxy stable https://charts.helm.sh/stable
POST_repository_helm_proxy cetic https://cetic.github.io/helm-charts
DELETE_repositories helm-hosted || true
DELETE_blobstores helm-hosted || true
POST_blobstores_file helm-hosted
POST_repositories helm/hosted @manifests/repository-helm-hosted.json

# NPM
DELETE_repositories npm-group || true
DELETE_repositories npm-proxy-npmjs.org || true
DELETE_repositories npm-hosted || true
DELETE_blobstores npm-hosted || true
DELETE_blobstores npm-default || true
POST_blobstores_file npm-hosted
POST_blobstores_file npm-default
POST_repositories npm/proxy @manifests/repository-npm-proxy-npmjs.org.json
POST_repositories npm/hosted @manifests/repository-npm-hosted.json
POST_repositories npm/group @manifests/repository-npm-group.json

# # apt
# POST_blobstores_file apt-hosted
# POST_blobstores_file apt

# kubectl patch --namespace=nexus service/nexus-nexus-repository-manager  --patch='
# spec:
#  ports:
#  - name: docker-group
#    port: 5001
#    protocol: TCP
#  - name: docker-hosted
#    port: 5000
#    protocol: TCP
#  - name: nexus-ui
#    port: 8081
#    protocol: TCP
#    targetPort: 8081
# '

# kubectl patch --namespace=nexus deployment/nexus-nexus-repository-manager --patch='
# spec:
#  template:
#    spec:
#      containers:
#      - name: nexus-repository-manager
#        ports:
#        - containerPort: 5001
#          name: docker-group
#          protocol: TCP
#        - containerPort: 5000
#          name: docker-hosted
#          protocol: TCP
#        - containerPort: 8081
#          name: nexus-ui
#          protocol: TCP
# '

# # nexus-nexus-repository-manager
# kubectl patch --namespace=nexus ingress/nexus-nexus-repository-manager --patch='
# metadata:
#  annotations:
#    cert-manager.io/cluster-issuer: ca-issuer
#    nginx.ingress.kubernetes.io/proxy-body-size: "0"
#    nginx.org/client-max-body-size: "0"
# spec:
#  tls:
#  - hosts:
#    - docker-proxy.home.arpa
#    - docker.home.arpa
#    - nexus.home.arpa
#    secretName: nexus-tls
#  rules:
#  - host: docker-proxy.home.arpa
#    http:
#      paths:
#      - path: "/"
#        pathType: ImplementationSpecific
#        backend:
#          service:
#            name: nexus-nexus-repository-manager
#            port:
#              number: 5001
#  - host: docker.home.arpa
#    http:
#      paths:
#      - path: "/"
#        pathType: ImplementationSpecific
#        backend:
#          service:
#            name: nexus-nexus-repository-manager
#            port:
#              number: 5000
#  - host: nexus.home.arpa
#    http:
#      paths:
#      - backend:
#          service:
#            name: nexus-nexus-repository-manager
#            port:
#              number: 8081
#        path: "/"
#        pathType: Prefix
# '
