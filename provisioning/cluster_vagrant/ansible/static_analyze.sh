#!/usr/bin/bash

files=$(find . -type f | grep -e yml -e yaml)

status=0

function updateStatus() {
    local newStatus=$1
    if [[ $newStatus -gt 0 ]]; then
      status=1
    fi
}

function ansible_lint() {
    local filename="${1}"
    ansible-lint "${filename}"
}

function yaml_lint() {
    local filename="${1}"
    yamllint "${filename}"
}

function verify() {
    local filename="${1}"
    ansible_lint "${filename}"
    updateStatus $?
    yaml_lint "${filename}"
    updateStatus $?
}

if [[ $# -eq 0 ]]; then
    for file in $files; do
        echo "${file}"
        verify "${file}"
    done
else
    $@
fi

exit $status