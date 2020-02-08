#!/usr/bin/env bash

up() {
    local path="$1"
    print_header "Composing infrastructure. path=${path}"
    cd "${path}" || exit 1
    # Detached mode: Run containers in the background, print new container names
    # Build images before starting containers
    docker-compose up \
    --detach \
    --build
    cd -  > /dev/null || exit 1
}

stop() {
    local path="$1"
    print_header "Stopping compose file. path=${path}"
    cd "${path}" || exit 1
    docker-compose stop
    cd -  > /dev/null || exit 1
}

clean() {
    local path="$1"
    print_header "Cleaning compose file. path=${path}"
    cd "${path}" || exit 1
    # Remove named volumes declared in the `volumes` section of the Compose file and anonymous volumes attached to containers.
    # Remove all images used by any service
    # --remove-orphans Remove containers for services not defined in the Compose file
    docker-compose down \
    --volumes \
    --rmi all
    cd -  > /dev/null || exit 1
}

show_logs() {
    local path="$1"
    print_header "Printing logs. path=${path}"
    print_message "[ Skip by Ctrl+C without killing containers]"
    cd "${path}" || exit 1
    # Follow log output
    # --timestamps Show timestamps
    # Number of lines to show from the end of the logs for each container
    docker-compose logs \
    --follow \
    --tail="all"
    cd -  > /dev/null || exit 1
}
