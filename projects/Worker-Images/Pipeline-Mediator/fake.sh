#!/usr/bin/env bash

npx json-schema-faker-cli <(cat "./schemas/${1}.yaml" | yq --output-format=json) | yq --input-format=json
