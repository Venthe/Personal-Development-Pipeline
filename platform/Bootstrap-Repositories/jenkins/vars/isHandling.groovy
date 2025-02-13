#!/usr/bin/env groovy

@Grapes([@Grab('com.fasterxml.jackson.core:jackson-databind:2.14.2'),
        @Grab('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2'),
        @Grab('org.jgrapht:jgrapht-core:1.5.1'),
        @Grab('com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2')])

import static eu.venthe.Shared.isHandling as iH

def call(String workflowFileName = null, Map workflow, Map input) {
    return iH(workflowFileName, workflow, input)
}
