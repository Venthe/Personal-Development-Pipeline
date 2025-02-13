#!/usr/bin/env bash

npm --quiet run build -- --quiet && node ./dist/index.js --config=./test/postgres/configuration.yml --state=./test/postgres/state.yml
