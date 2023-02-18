import spock.lang.Specification

import static eu.venthe.Shared.buildDependencyTree
import static eu.venthe.Shared.parseYaml

class DependencyTreeTest extends Specification {
    static {
        System.setProperty("groovy.grape.enable", "false")
    }

    def "1"() {
        given:
        def jobs = parseYaml """\
        d:
         needs:
           - a
           - b
        a: {}
        b:
          needs: a
        c:
          needs: a
        """

        when:
        def result = buildDependencyTree(jobs)

        then:
        result == [
                ["a"],
                ["b", "c"],
                ["d"]
        ]
    }

    def "2"() {
        given:
        def jobs = parseYaml """\
        a: {}
        """

        when:
        def result = buildDependencyTree(jobs)

        then:
        result == [
                ["a"]
        ]
    }

    def "3"() {
        given:
        def jobs = parseYaml """\
        d:
         needs:
           - a
        a: {}
        b:
          needs: a
        c:
          needs: a
        """

        when:
        def result = buildDependencyTree(jobs)

        then:
        result == [
                ["a"],
                ["b", "c", "d"]
        ]
    }

    def "4"() {
        given:
        def jobs = parseYaml """\
        d:
         needs:
           - a
        a: {}
        b:
          needs:
            - a
            - d
        c:
          needs:
            - a
            - d
        """

        when:
        def result = buildDependencyTree(jobs)

        then:
        result == [
                ["a"],
                ["d"],
                ["b", "c"]
        ]
    }

    def "5"() {
        given:
        def jobs = parseYaml """\
        d:
         needs:
           - a
        a: {}
        b:
          needs:
            - d
        c:
          needs:
            - b
        """

        when:
        def result = buildDependencyTree(jobs)

        then:
        result == [
                ["a"],
                ["d"],
                ["b"],
                ["c"]
        ]
    }
}
