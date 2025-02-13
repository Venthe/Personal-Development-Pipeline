import spock.lang.Specification

import static eu.venthe.Shared.*

class IsHandlingTest extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    def "not null 1"() {
        given:
        def input = parseYaml """\
        """

        when:
        boolean result = isHandling([:], input)

        then:
        def e = thrown Exception
        e.message == "Input should not be null"
    }

    def "not null 2"() {
        given:
        def workflow = parseYaml """\
        """

        when:
        boolean result = isHandling(null, workflow, [:])

        then:
        def e = thrown Exception
        e.message == "Workflow should not be null"
    }

    def "No on property"() {
        given:
        def input = parseYaml """\
        any: 1
        """
        def workflow = parseYaml """\
        any: 1
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        def e = thrown Exception
        e.message == "There is no \"on\" property, this workflow will never run"
    }

    def "Single event"() {
        given:
        def input = parseYaml """\
        type: patchset-created
        """
        def workflow = parseYaml """\
        on: patchset-created
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Multiple events"() {
        given:
        def input = parseYaml """\
        type: patchset-created
        """
        def workflow = parseYaml """\
        on: [patchset-created]
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 1"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            branches:
              - main
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 2"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            branches:
              - ma*
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 3"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            branches:
              - lorem
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == false
    }

    def "Activity type: ref-updated 4"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            branches:
              - lorem
              - main
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 4.1"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: refs/heads/main
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            branches:
              - '**main'
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 4.2"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
            
        """
        def workflow = parseYaml '''\
        on:
          ref-updated:
            branches:
              - '**main'
        '''

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 5"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
        additionalProperties:
          files:
            "a.yaml":
              status: A
              lines_deleted: -5
              size_delta: 5
              size: 5
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            paths:
              - '**.yaml'
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Activity type: ref-updated 6"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        metadata:
          branchName: main
        additionalProperties:
          files:
            "a.yaml":
              status: A
              lines_deleted: -5
              size_delta: 5
              size: 5
            
        """
        def workflow = parseYaml """\
        on:
          ref-updated:
            paths:
              - '**.other'
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == false
    }

    def "Workflow call"() {
        given:
        def input = parseYaml """\
        type: workflow-call
        """
        def workflow = parseYaml """\
        on:
          workflow-call:
            inputs:
              username:
                description: 'A username passed from the caller workflow'
                default: 'john-doe'
                required: false
                type: string
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == true
    }

    def "Workflow call 2"() {
        given:
        def input = parseYaml """\
        type: patchset-created
        """
        def workflow = parseYaml """\
        on:
          workflow-call:
            inputs:
              username:
                description: 'A username passed from the caller workflow'
                default: 'john-doe'
                required: false
                type: string
        """

        when:
        boolean result = isHandling(null, workflow, input)

        then:
        result == false
    }

    def "Workflow dispatch 1"() {
        given:
        def input = parseYaml """\
        type: workflow-dispatch
        additionalProperties:
            workflow: build
        """
        def workflow = parseYaml """\
        on:
          workflow-dispatch:
            inputs:
              username:
                description: 'A username passed from the caller workflow'
                default: 'john-doe'
                required: false
                type: string
        """

        when:
        boolean result = isHandling(".pipeline/workflows/build.yml", workflow, input)

        then:
        result == true
    }

    def "Workflow dispatch 2"() {
        given:
        def input = parseYaml """\
        type: workflow-dispatch
        additionalProperties:
            workflow: build2
        """
        def workflow = parseYaml """\
        on:
          workflow-dispatch:
            inputs:
              username:
                description: 'A username passed from the caller workflow'
                default: 'john-doe'
                required: false
                type: string
        """

        when:
        boolean result = isHandling(".pipeline/workflows/build.yml", workflow, input)

        then:
        result == false
    }

    def "Workflow dispatch 1.1"() {
        given:
        def input = parseYaml """\
        type: workflow-dispatch
        additionalProperties:
            workflow: build
        """
        def workflow = parseYaml """\
        on:
          workflow-dispatch: {}
        """

        when:
        boolean result = isHandling(".pipeline/workflows/build.yml", workflow, input)

        then:
        result == true
    }
}
