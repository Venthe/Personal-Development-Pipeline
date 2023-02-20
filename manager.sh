#!/usr/bin/env bash

set -o pipefail
set -o errexit

. ./.env

list_tags() {
  ansible-playbook ./provision-kubernetes.yml --list-tags
}

function provision() {
  ansible-playbook \
    ./provision-kubernetes.yml \
    --inventory-file ./kubernetes.inventory.yml \
    --user ${PLAY_USERNAME} \
    --ask-become \
    "${@}"
}

function ansible_lint() {
  local filename="${1}"
  ansible-lint "${filename}"
}

function yaml_lint() {
  local filename="${1}"
  yamllint "${filename}"
}

function ansible_syntax_check() {
  local filename="${1}"
  ansible-playbook "${filename}" --syntax-check
}

function validate() {
  files=$(find . -type f | grep -e yml -e yaml)
  for file in $files; do
    echo "${file}"
    ansible_syntax_check "${file}"
  done
}

function lint() {
  function updateStatus() {
    local newStatus=$1
    if [[ $newStatus -gt 0 ]]; then
      status=1
    fi
  }

  files=$(find . -type f | grep -e yml -e yaml)

  status=0
  for file in $files; do
    echo "${file}"

    local filename="${1}"
    ansible_lint "${filename}"
    updateStatus $?
    yaml_lint "${filename}"
    updateStatus $?
  done
  exit $status
}

function download_all_helm() {
  helm repo add argo https://argoproj.github.io/argo-helm
  helm repo add bitnami https://charts.bitnami.com/bitnami
  helm repo add cert-utils-operator https://redhat-cop.github.io/cert-utils-operator
  helm repo add cetic https://cetic.github.io/helm-charts
  helm repo add coredns https://coredns.github.io/helm
  helm repo add cowboysysop https://cowboysysop.github.io/charts/
  helm repo add democratic-csi https://democratic-csi.github.io/charts/
  helm repo add grafana https://grafana.github.io/helm-charts
  helm repo add hashicorp https://helm.releases.hashicorp.com
  helm repo add helm-stable https://charts.helm.sh/stable
  helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
  helm repo add jenkins https://charts.jenkins.io
  helm repo add jetstack https://charts.jetstack.io
  helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
  helm repo add loki https://grafana.github.io/loki/charts
  helm repo add nexus https://nexus.home.arpa/repository/helm-hosted/
  helm repo add opensearch https://opensearch-project.github.io/helm-charts
  helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
  helm repo add rook-release https://charts.rook.io/release
  helm repo add sonatype https://sonatype.github.io/helm3-charts/
  helm repo add stakater https://stakater.github.io/stakater-charts
  helm repo add vmware-tanzu https://vmware-tanzu.github.io/helm-charts

  helm repo update
}

${@}
