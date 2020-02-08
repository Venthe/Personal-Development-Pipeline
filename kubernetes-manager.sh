#!/usr/bin/env bash

. ./utils/bootstrap.sh
bootstrap "$@"

print_pelp() {
    help_header ""
    help_description "Manages kubernetes in Docker for Windows"
    help_options
    help_parameter "help" "Prints this message"
    help_parameter "dashboard [OPTION]" "install\ndelete\nreinstall"
    help_parameter "proxy" "starts kubernetes proxy"
    execution_parameters_help
    echo ""
}

print_help_with_no_parameters "$@"

# Load library scripts
scripts_path="${KUBERNETES_DIRECTORY}/scripts"
load_script "$scripts_path" "utils"
load_script "$scripts_path" "namespaces"
load_script "$scripts_path" "pods"
load_script "$scripts_path" "services"
load_script "$scripts_path" "deployments"
load_script "$scripts_path" "resources"

load_environment_variables "$KUBERNETES_DIRECTORY"

standard_menu_entry() {
    local choice=$1
    local path=$2
    local app_name=$3
    case "$choice" in
    create)
        deploy_app "$path" "$app_name"
        ;;
    wipe)
        wipe_app "$app_name"
        ;;
    recreate)
        menu "$app_name" "wipe"
        menu "$app_name" "create"
        ;;
    *)
        print_error "Incorrect submenu selected!"
        exit 1;
        ;;
    esac
}

menu() {
    local path="$KUBERNETES_DIRECTORY/$APPS_DIRECTORY"
    case "$1" in
    create)
        menu "dashboard" "create"
        menu "metrics" "create"
        menu "nexus" "create"
        menu "gerrit" "create"
        menu "sonarqube" "create"
        menu "jenkins" "create"
        menu "pgadmin" "create"
        ;;
    wipe)
        menu "dashboard" "wipe"
        menu "metrics" "wipe"
        menu "nexus" "wipe"
        menu "gerrit" "wipe"
        menu "sonarqube" "wipe"
        menu "jenkins" "wipe"
        menu "pgadmin" "wipe"
        ;;
    recreate)
        menu "dashboard" "recreate"
        menu "metrics" "recreate"
        menu "nexus" "recreate"
        menu "gerrit" "recreate"
        menu "sonarqube" "recreate"
        menu "jenkins" "recreate"
        menu "pgadmin" "recreate"
        ;;
    sonarqube)
        standard_menu_entry "$2" "$path" "sonarqube"
        ;;
    nexus)
        standard_menu_entry "$2" "$path" "nexus"
        ;;
    jenkins)
        standard_menu_entry "$2" "$path" "jenkins"
        ;;
    gerrit)
        standard_menu_entry "$2" "$path" "gerrit"
        ;;
    openldap)
        standard_menu_entry "$2" "$path" "openldap"
        ;;
    pgadmin)
        standard_menu_entry "$2" "$path" "pgadmin"
        ;;
    metrics)
        local app_name="metrics"
        case "$2" in
        create)
            deploy_app "$path/monitoring" "monitoring-namespace"
            deploy_app "$path/monitoring" "prometheus-rbac"
            deploy_app "$path/monitoring" "prometheus-config"
            deploy_app "$path/monitoring" "prometheus-service"
            deploy_app "$path/monitoring" "prometheus-deployment"
            deploy_app "$path/monitoring" "grafana-service"
            deploy_app "$path/monitoring" "grafana-deployment"
            deploy_app "$path/monitoring" "node-exporter"
            deploy_app "$path/monitoring" "state-metrics-deployment"
            deploy_app "$path/monitoring" "state-metrics-rbac"
            ;;
        wipe)
            delete_namespace "monitoring"
            ;;
        recreate)
            menu "$app_name" "wipe"
            menu "$app_name" "create"
            ;;
        esac
        ;;
    dashboard)
        local app_name="dashboard"
        case "$2" in
        create)
            deploy_app "$path/dashboard" "dashboard-recommended"
            deploy_app "$path/dashboard" "dashboard-accounts"
            deploy_app "$path/dashboard" "dashboard-configuration"
            menu "$app_name" "token"
            ;;
        wipe)
            delete_namespace "kubernetes-dashboard"
            ;;
        token)
            get_dashboard_bearer_token
            ;;
        recreate)
            menu "$app_name" "wipe"
            menu "$app_name" "create"
            ;;
        esac
        ;;
    help)
        print_help
        exit 0
        ;;
    pods)
        get_all_pods
        ;;
    ports)
        get_node_ports
        ;;
    proxy)
        start_proxy
        ;;
    *)
        print_help
        exit 1
        ;;
    esac
}

# Run main script
main "$@"
