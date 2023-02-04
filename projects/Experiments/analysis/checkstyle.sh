#!/usr/bin/env bash

mkdir -p ./build/reports

java -jar ./.analyzers/checkstyle/checkstyle.jar \
  -f sarif \
  -c /sun_checks.xml \
  -o ./build/reports/checkstyle.sarif ./src/main/java ./src/test/java
