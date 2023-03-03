#!/usr/bin/env bash

set -o errexit
set -o pipefail

2>/dev/null bash ./pipeline.sh build_container

function _test() {
  echo "Running test ${1}"
  bash ./pipeline.sh test_container ${1}
}

_test 1
_test 2
_test 3
_test 4
_test 5
_test 6
