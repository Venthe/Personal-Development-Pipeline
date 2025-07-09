#!/usr/bin/env bash

vault auth enable kubernetes

vault write auth/kubernetes/config \
   disable_iss_validation="true" \
   token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
   kubernetes_host=https://${KUBERNETES_PORT_443_TCP_ADDR}:443 \
   kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

vault write auth/kubernetes/role/readonly-infrastructure \
    bound_service_account_names=* \
    bound_service_account_namespaces=* \
    policies=readonly-infrastructure \
    token_max_ttl=20m \
    ttl=10m
