#!/usr/bin/env bash

dockerd > ~/dockerd.log 2>&1 &

function run() {
  node /runner/index.js ${@}
}

${@}
