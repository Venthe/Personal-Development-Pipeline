#!/usr/bin/env bash


mkdir -p ./build/reports

project_name=`./gradlew --quiet --console=plain compileClasspath`

./.analyzers/dependency-check/bin/dependency-check.sh \
 --format SARIF \
 --project "${project_name}" \
 --out ./build/reports/dependency-analysis.sarif \
 --scan ./build/libs/**/*.jar
