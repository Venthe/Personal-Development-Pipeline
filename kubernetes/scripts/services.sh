#!/usr/bin/env bash

delete_all_services() {
  local namespace="$1"
  print_header "Deleting all services. namespace='$namespace'"
  kubectl --namespace "$namespace" \
    delete svc \
    --all
}

create_service() {
  local namespace_prefix="$1"
  local service_name="$2"
  local services_path="$3"
  local script_name="$4"
  local namespace_full_name

  namespace_full_name=$(get_namespace_name "$namespace_prefix" "$service_name")
  print_header "Creating service. services_path=$services_path, namespace_full_name=$namespace_full_name, script_name=$script_name"

  cd "$services_path/$service_name" || exit 1
  create_namespace "$namespace_prefix" "$service_name"
  . "$script_name".sh \
    "$namespace_prefix" \
    "$namespace_full_name" \
    "$service_name"
  cd -  > /dev/null || exit 1
}

list_services() {
  kubectl get services --output=jsonpath="{range .items[?(@.metadata.labels.app=='$1')]} {.metadata.name}{end}"
}