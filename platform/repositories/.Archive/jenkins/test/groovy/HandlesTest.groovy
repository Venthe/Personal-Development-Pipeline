import spock.lang.Specification

import static eu.venthe.Shared.*

class HandlesTest extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    def "on null"() {
        given:
        def input = eu.venthe.Shared.parseYaml """\
        """

        when:
        def result = eu.venthe.Shared.handles(null, input)

        then:
        result == false
    }

    def "list handles"() {
        given:
        def input = parseYaml """\
        type: patchset-created
        """

        when:
        def result = handles(["patchset-created"], input)

        then:
        result == true
    }

    def "list not handles"() {
        given:
        def input = parseYaml """\
        type: ref-updated
        """

        when:
        def result = handles(["patchset-created"], input)

        then:
        result == false
    }
}
