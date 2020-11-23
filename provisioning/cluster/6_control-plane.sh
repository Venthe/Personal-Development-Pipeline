#!/usr/bin/env bash

# Exit immediately if a pipeline returns a non-zero status.
set -o errexit
# Print a trace of simple commands
set -o xtrace

kubectl config view --raw
