#!/usr/bin/env bash

classpath=`./gradlew --quiet --console=plain compileClasspath`

mkdir -p ./build/reports
#  -debug \

./.analyzers/spotbugs/bin/spotbugs \
  -sarif \
  -textui \
  -auxclasspath "${classpath}" \
  ./build/libs/analysis-0.0.1-SNAPSHOT-plain.jar \
  > ./build/reports/spotbugs.sarif
