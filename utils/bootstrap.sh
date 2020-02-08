#!/usr/bin/env bash

export VERBOSE
export NORMAL
export BOLD
export UNDERLINE
export STANDOUT_MODE

NORMAL=$(tput sgr0)
BOLD=$(tput bold)
UNDERLINE=$(tput smul)
STANDOUT_MODE=$(tput smso)
VERBOSE=
SCRIPT_NAME="$0"

# FIXME: Removes colour codes
indent() {
    sed --unbuffered 's/^/  /'
}

clear_last_line() {
    tput cuu 1 && tput el
}

# FIXME: Does not work with loading variables?
suppress() {
    sed 's/^.*$//' | clear_last_line
}

print_error() {
    local message="$1"
    echo "! $message"
}

print_message() {
    local message="$1"
    echo "** $message"
}

print_header() {
    local message="$1"
    echo -e "\n*  $message"
}

source_file() {
    # local path_and_filename="$1"
    if test -f "$path_and_filename"; then
        # shellcheck source=/dev/null
        . "$path_and_filename"
    else
        print_error "$path_and_filename not found"
    fi
}

load_environment_variables() {
    local path="$1"
    local filename="${2:-.env}"
    local path_and_filename="$path/$filename"
    # print_header "Loading environment variables. path=$path_and_filename"
    source_file "$path_and_filename"
}

load_script() {
    local path="$1"
    local filename="$2.sh"
    local path_and_filename="$path/$filename"
    # print_header "Loading script. path=${path_and_filename}"
    source_file "$path_and_filename"
}

early_exit_on_error() {
    set -e
}

print_commands() {
    set -x
}

set_execution_parameters() {
    for i in "$@"; do
        case "$i" in
        -v | --verbose)
            VERBOSE=true
            print_commands
            ;;
        -e | --early-exit)
            early_exit_on_error
            ;;
        *) ;;

        esac
    done
}

help_header() {
    local script_name="$SCRIPT_NAME"
    local usage="$1"
    echo "Usage: ${BOLD}${script_name}${NORM} ${usage}"
}

help_description() {
    local description="$1"
    echo "$description"
}

help_options() {
    echo ""
    echo "Options:"
}

help_parameter() {
    local command="$1"
    local description="$2"
    echo "$BOLD$command$NORMAL"
    echo -e "$description"
}

execution_parameters_help() {
    help_parameter "-v, --verbose" "Sets verbose mode"
    help_parameter "-e, --early-exit" "Exits early on error"
}

print_help() {
    help_header "$@"
    help_options
    execution_parameters_help
    echo ""
}

print_help_with_no_parameters() {
    if [ $# -eq 0 ]; then
        print_help "$@"
        exit 0
    fi
}

bootstrap() {
    set_execution_parameters "$@"
    load_environment_variables .
}

main() {
    local all_args=($@)
    local len=${#all_args[@]}
    for i in $(seq 0 $(($len - 1))); do
        if [[ "${all_args[$i]:0:1}" == "-" ]]; then
            continue
        fi

        menu "${all_args[@]:$i}"
        exit 0
    done
}
