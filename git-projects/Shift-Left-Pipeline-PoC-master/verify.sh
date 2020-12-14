#!/usr/bin/bash

DEBUG=${DEBUG:-false}
LOG_LEVEL=${LOG_LEVEL:-"-1"}
EARLY_EXIT=${EARLY_EXIT:-false}
COLOR=${COLOR:-false}
EXIT_STATUS=0

set -o pipefail

if [[ ${DEBUG} == true ]]; then
    set -x
fi

if [[ ${EARLY_EXIT} == true ]]; then
    set -e
fi

function color() {
    function colorize() {
        local code="$1"; shift

        if [[ ${COLOR} == true ]]; then
            printf "\e[%sm%s\e[0m" "${code}" "$@"
        else
            printf "%s" "$@"
        fi
    }

    local colour="$1"; shift
    case "${colour}" in
        red)
            colorize 31 "$@"
        ;;
        white)
            colorize 97 "$@"
        ;;
        yellow)
            colorize 33 "$@"
        ;;
        magenta)
            colorize 35 "$@"
        ;;
        darkgray)
            colorize 90 "$@"
        ;;
        default)
            colorize 39 "$@"
        ;;
        *)
            colorize 97 "$@"
        ;;
    esac
}

function print() {
    function log() {
        local value="${1}"
        local color="${2}"
        local text="${3}"
        shift 3
        if [[ ${LOG_LEVEL} -ge ${value} ]]; then
            local cmd="color ${color} [${text}] "
            echo -e "$(${cmd})" "$@"
        fi
    }

    local log_level="${1}";shift
    case "${log_level}" in
        trace)
            log 12 "darkgray" "TRACE" "$@"
            ;;
        debug)
            log 9 "magenta" "DEBUG" "$@"
            ;;
        warn)
            log 6 "yellow" "WARN" "$@"
            ;;
        info)
            log 3 "default" "INFO" "$@"
            ;;
        error)
            log 0 "red" "ERROR" "$@"
            ;;
        *)
            log 0 "default" "UNDEFINED" "$@"
            ;;
    esac
}

function save_exit_status() {
    local exit_status="$1"
    print debug "Existing exit status: ${EXIT_STATUS}. New exit status: ${exit_status}."
    if [[ ${exit_status} -gt 0 ]]; then
        if [[ ${EXIT_STATUS} -gt 0 ]]; then
            print warn "Current exit status ${EXIT_STATUS} is an error but it will be changed"
        fi
        EXIT_STATUS="${exit_status}"
        print debug "Exit status changed to ${EXIT_STATUS}"
    fi
}

function exit_with_status() {
    local exit_status="${1}"
    local text="Process done with status ${1}"

    if [[ ${exit_status} -gt 0 ]]; then
        print error "${text}"
    else
        print debug "${text}"
    fi
    exit "${exit_status}"
}

function lint() {
    function markdown() {
        print info "Linting markdown"
        docker run \
            --interactive \
            --rm \
            --volume="$(pwd):/work" \
            tmknom/markdownlint './**/*.md' \
            | cat \
            | sed -E 's/\s+/,/' \
            | sed -E 's/\s+/,/' \
            | sed -E 's/:+/,/' \
            | jq \
                --raw-input \
                'split(",")| .[1] |= split(":")|{file:.[0], line:.[1][0], column:.[1][1], errorCode:.[2], description:.[3]}' \
            | jq \
                --slurp \
                --monochrome-output \
                '.'
        
        save_exit_status "$?"
    }

    function shell() {
        print info "Linting shell"

        local color;
        case "${COLOR}" in
            true)
                color=always
                ;;
            *)
                color=never
                ;;
        esac;

        find . -name "*.sh" -type f -print0 \
            | xargs -0 docker run \
            --interactive \
            --rm \
            --volume="$(pwd):/mnt" \
            koalaman/shellcheck \
                --color="${color}" \
                --enable=all \
                --shell=bash \
                --format=json1 \
            | cat \
            | jq \
                --monochrome-output \
                '.comments'
        
        save_exit_status "$?"
    }

    function ansible() {
        docker run \
            --interactive \
            --rm \
            --volume=$(pwd):/data \
            cytopia/ansible-lint \
            ./**/*ansible.yml \
                --parseable-severity \
                --show-relpath \
                --nocolor \
                -f plain \
            | cat \
            | sed -E 's/:+/ /' \
            | jq \
                --raw-input \
                'split(" ")| .[1] |= split(":")|{file:.[0], line:.[1][0], column:.[1][1], errorCode:.[2], severity:.[3], description:.[4]}' \
            | jq \
                --slurp \
                --monochrome-output \
                '.'
        
        save_exit_status "$?"
    }

    function yaml() {
         # TODO: Add error code to the output
         docker run \
            --interactive \
            --rm \
            --volume=$(pwd):/data \
            cytopia/yamllint \
            . \
                -f parsable \
            | cat \
            | sed -E 's/:+/ /' \
            | jq \
                --raw-input \
                'capture("(?<filename>[^ ]+) (?<line>[0-9]+):(?<column>[0-9]+): \\[(?<severity>[^\\]]+)\\] (?<description>.+)")' \
            | jq \
                --slurp \
                --monochrome-output \
                '.'
        save_exit_status "$?"
    }

    "$@"
}

print debug "Environment variables:"
print debug "\tDEBUG=${DEBUG}"
print debug "\tEARLY_EXIT=${EARLY_EXIT}"
print debug "\tLOG_LEVEL=${LOG_LEVEL}"
print debug "\tCOLOR=${COLOR}"

"$@"

exit_with_status "${EXIT_STATUS}"