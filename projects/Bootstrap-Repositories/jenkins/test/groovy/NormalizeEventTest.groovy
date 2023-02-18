import spock.lang.Specification

import static eu.venthe.Shared.normalizeEventName

class NormalizeEventTest extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    def "1"() {
        when:
        def result = normalizeEventName("patchset-created")

        then:
        result == "patchsetcreated"
    }
}
