#!/usr/bin/env bash

. ./utils/bootstrap.sh
bootstrap "$@"

print_help() {
    help_header ""
    help_description "Manages kubernetes in Docker for Windows"
    help_options
    help_parameter "help" "Prints this message"
    help_parameter "dashboard [OPTION]" " create\n wipe\n recreate"
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

temp_path="${KUBERNETES_DIRECTORY}/${TEMP_DIRECTORY}"

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
        exit 1
        ;;
    esac
}

menu() {
    local path="$KUBERNETES_DIRECTORY/$APPS_DIRECTORY"
    case "$1" in
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
    xwiki)
        standard_menu_entry "$2" "$path" "xwiki"
        ;;
    redmine)
        standard_menu_entry "$2" "$path" "redmine"
        ;;
    ghost)
        standard_menu_entry "$2" "$path" "ghost"
        ;;
    metrics)
        local app_name="monitoring"
        local grafana_temp="$temp_path/$app_name"
        case "$2" in
        create)
            menu "$app_name" "generateCertificates"
            deploy_app "$path" "$app_name"
            ;;
        wipe)
            menu "$app_name" "wipeCertificates"
            delete_namespace "$app_name"
            ;;
        recreate)
            menu "$app_name" "wipe"
            menu "$app_name" "create"
            ;;
        generateCertificates)
            generate_kube_certificates "$grafana_temp"
            ;;
        wipeCertificates)
            rm -rf "$grafana_temp"
            ;;
        esac
        ;;
    dashboard)
        local app_name="dashboard"
        local namespace="dashboard-namespace"
        case "$2" in
        create)
            deploy_app "$path" "$app_name"
            ;;
        wipe)
            delete_namespace "$namespace"
            ;;
        recreate)
            menu "$app_name" "wipe"
            menu "$app_name" "create"
            ;;
        token)
            get_dashboard_bearer_token "$namespace"
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
