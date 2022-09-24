#!/usr/bin/env bash

yamllint "./docker-compose.yml"
docker compose config >/dev/null
shellcheck "./*.sh"
