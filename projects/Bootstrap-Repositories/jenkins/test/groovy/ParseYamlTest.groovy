import spock.lang.Specification

import static eu.venthe.Shared.parseYaml

class ParseYamlTest extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    def "Simple yaml"() {
        given:
        def yaml = """\
        test:
          a: 2
        """

        when:
        def result = parseYaml(yaml.stripMargin().stripIndent())

        then:
        result["test"]["a"] == 2
    }

    def "Change event parse"() {
        given:
        def text = Utilities.load("change-event.json")

        when:
        def result = parseYaml(text)

        then:
        print result
    }
}
