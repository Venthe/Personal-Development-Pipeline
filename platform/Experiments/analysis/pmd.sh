#!/usr/bin/env bash

mkdir -p ./build/reports

./.analyzers/pmd/bin/run.sh pmd \
  --dir ./src/main/java \
  --rulesets rulesets/java/quickstart.xml,category/java/codestyle.xml \
  --format sarif \
  --short-names \
  > ./build/reports/pmd.sarif
#--help
