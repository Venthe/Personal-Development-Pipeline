#!/usr/bin/env bash

delete_namespaces() {
    local namespace_prefix="$1"
    print_header "Deleting resources. namespace_prefix=$namespace_prefix"

    # FIXME: NAMESPACE appears unused
    kubectl get namespace \
        --no-headers=true |
        awk '{print $1}' |
        grep "^$namespace_prefix" |
        while read -r NAMESPACE; do
            # delete-all-pods "$namespace_prefix" | indent
            # delete-all-services "$namespace_prefix" | indent
            delete_namespace "$namespace_prefix" | indent
        done
}
