#!/usr/bin/env bash

get_namespaces() {
    print_header "Listing all kubernetes namespaces."
    kubectl -v=1 get namespace
}

get_prefixed_namespaces() {
    local namespace_prefix=$1
    print_header "Listing prefixed kubernetes namespaces. namespace_prefix=$namespace_prefix"
    kubectl -v=1 get namespace \
        --no-headers=true |
        awk '{print $1}' |
        grep "^$namespace_prefix"
}

delete_namespace() {
    local namespace=$1
    print_header "Deleting namespace. namespace=$namespace"
    kubectl -v=3 delete \
        namespace "$namespace"
}

create_namespace() {
    local namespace_prefix=$1
    local namespace_name=$2
    local namespace_full_name
    namespace_full_name=$(get_namespace_name "$namespace_prefix" "$namespace_name")
    print_header "Creating namespace. namespace_full_name=$namespace_full_name"
    kubectl -v=1 create namespace "$namespace_full_name"
}
