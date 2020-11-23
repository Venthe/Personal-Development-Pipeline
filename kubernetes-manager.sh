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
        exit
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
    dashboard)
        local app_name="dashboard"
        local namespace="kubernetes-dashboard"
        case "$2" in
        token)
            get_dashboard_bearer_token "$namespace"
            ;;
        *)
            standard_menu_entry "$2" "$path" "dashboard"
            ;;
        esac
        ;;
    gerrit)
        standard_menu_entry "$2" "$path" "gerrit"
        ;;
    ghost)
        standard_menu_entry "$2" "$path" "ghost"
        ;;
    jenkins)
        standard_menu_entry "$2" "$path" "jenkins"
        ;;
    kube-system)
        standard_menu_entry "$2" "$path" "kube-system"
        ;;
    ldap)
        standard_menu_entry "$2" "$path" "ldap"
        ;;
    monitoring)
        standard_menu_entry "$2" "$path" "monitoring"
        ;;
    nexus)
        standard_menu_entry "$2" "$path" "nexus"
        ;;
    pgadmin)
        standard_menu_entry "$2" "$path" "pgadmin"
        ;;
    phpmyadmin)
        standard_menu_entry "$2" "$path" "phpmyadmin"
        ;;
    plantuml)
        standard_menu_entry "$2" "$path" "plantuml"
        ;;
    redmine)
        standard_menu_entry "$2" "$path" "redmine"
        ;;
    sonarqube)
        standard_menu_entry "$2" "$path" "sonarqube"
        ;;
    xwiki)
        standard_menu_entry "$2" "$path" "xwiki"
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
    metallb)
        reset_metallb
        ;;
    *)
        print_help
        exit 1
        ;;
    esac
}

# Run main script
main "$@"
