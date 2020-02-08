#!/usr/bin/env bash

get_namespace_name() {
    local namespace_prefix="$1"
    local namespace_name="$2"
    echo "$namespace_prefix-$namespace_name"
}

override_namespace() {
    local filename="$1"
    local new_namespace="$2"

    sed 's/namespace:.*/namespace: '"$new_namespace"'/' ./"$filename" | cat
}

update_namespace() {
    local filename="$1"
    local new_namespace="$2"

    sed -i 's/namespace:.*/namespace: '"$new_namespace"'/' ./"$filename"
}

get_node_ports() {
    print_header "Listing NodePorts"
    local host_name=$(kubectl config view --output=jsonpath="{range .clusters[*]}{.cluster.server}{'\n'}{end}" | awk -F[/:] '{print $4}')
    kubectl --output=jsonpath="{range .items[?(.spec.type=='NodePort')]}{.metadata.name}{'\n'}{range .spec.ports[*]}{' * ['}{.name}{'] http://'}{'${host_name}'}:{.nodePort}/{'\n'}{end}{'\n'}{end}" \
        get services \
        --all-namespaces
}

start_proxy() {
    print_header "Starting proxy"
    local port="8001"
    kubectl cluster-info
    echo "http://localhost:${port}/api/v1/services"
    echo "Remember to add /proxy to service name"
    kubectl proxy --port="$port"
}

get_dashboard_bearer_token() {
    kubectl get secret $(kubectl get secret --namespace=kubernetes-dashboard | grep admin-user | awk '{print $1}') \
        --namespace=kubernetes-dashboard \
        --output=jsonpath="{.metadata.name}{'\t'}{.data.token}{'\n'}" |
        awk '{system("echo "$1"; echo -n "$2" | base64 --decode")}'
    kubectl get secret $(kubectl get secret --namespace=kubernetes-dashboard | grep admin-user | awk '{print $1}') \
        --namespace=kubernetes-dashboard \
        --output=jsonpath="{range .items[*]}{.metadata.name}{'\t'}{.data.token}{'\n'}{end}" |
        awk '{system("echo "$1"; echo -n "$2" | base64 --decode")}'
}
