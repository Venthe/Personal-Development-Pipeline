#!/usr/bin/env bash

. ./utils/bootstrap.sh
bootstrap "$@"

print_help() {
    help_header "[OPTIONAL_PARAMETERS] [OPTION] [PARAMETER]"
    help_description "Manages docker compose files in infrastructure"
    help_options
    help_parameter "up [PROJECT_DIRECTORY]" "Starts composer"
    help_parameter "stop [PROJECT_DIRECTORY]" "Stops composer"
    help_parameter "clean [PROJECT_DIRECTORY]" ""
    help_parameter "restart [PROJECT_DIRECTORY]" ""
    help_parameter "logs [PROJECT_DIRECTORY]" "Follow on single composer logs"
    help_parameter "help" "Prints this message"
    execution_parameters_help
    echo ""
}

print_help_with_no_parameters "$@"

# Sourcing
scripts_path="${DOCKER_DIRECTORY}/scripts"
load_script "$scripts_path" "docker-compose"

load_environment_variables "$DOCKER_DIRECTORY"

menu() {
    case "$1" in
    logs)
        local project_directory="$2"
        local path="${DOCKER_DIRECTORY}/${project_directory}"
        show_logs "$path"         ;;
    stop)
        local project_directory="$2"
        local path="${DOCKER_DIRECTORY}/${project_directory}"
        stop "$path"         ;;
    clean)
        local project_directory="$2"
        local path="${DOCKER_DIRECTORY}/${project_directory}"
        clean "$path"         ;;
    up)
        local project_directory="$2"
        local path="${DOCKER_DIRECTORY}/${project_directory}"
        up "$path"         ;;
    restart)
        local project_directory="$2"
        main_executor stop "$project_directory"
        main_executor up "$project_directory"
        ;;
    recreate)
        local project_directory="$2"
        menu stop "$project_directory"
        menu clean "$project_directory"
        menu up "$project_directory"
        ;;
    help)
        print_help
        exit 0
        ;;
    *)
        print_help
        exit 1
        ;;
    esac
}

# Run main script
main "$@"
