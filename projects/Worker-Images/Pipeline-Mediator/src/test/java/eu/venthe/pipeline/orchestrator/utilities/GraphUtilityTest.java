package eu.venthe.pipeline.orchestrator.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static eu.venthe.pipeline.orchestrator.utilities.GraphUtility.buildDependencyTree;
import static java.util.List.of;

class GraphUtilityTest {
    @Test
    void test1() {
        // given
        var jobs = parseYaml("""
                d:
                 needs:
                   - a
                   - b
                a: {}
                b:
                  needs: a
                c:
                  needs: a
                """);

        // when
        var result = buildDependencyTree(jobs);

        // then
        Assertions.assertThat(result).isEqualTo(of(of("a"), of("b", "c"), of("d")));
    }

    void test2() {
        // given
        var jobs = parseYaml("""
        a: {}
        """);

        // when
        var result = buildDependencyTree(jobs);

        // then

        Assertions.assertThat(result).isEqualTo(of(of("a")));
    }

    void test3() {
        // given
        var jobs = parseYaml("""
        d:
         needs:
           - a
        a: {}
        b:
          needs: a
        c:
          needs: a
        """);

        // when
        var result = buildDependencyTree(jobs);

        // then
        Assertions.assertThat(result).isEqualTo(of(of("a"), of("b", "c", "d")));
    }

    void test4() {
        // given
        var jobs = parseYaml("""
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
        """);

        // when
        var result = buildDependencyTree(jobs);

        // then
        Assertions.assertThat(result).isEqualTo(of(of("a"), of("d"), of("b", "c")));
    }

    void test5() {
        // given
        var jobs = parseYaml("""
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
        """);

        // when
        var result = buildDependencyTree(jobs);

        // then
        Assertions.assertThat(result).isEqualTo(of(of("a"), of("d"), of("b"), of("c")));
    }

    @SneakyThrows
    public static Set<GraphUtility.JobRequirements> parseYaml(String value) {
        ObjectNode nodes = (ObjectNode) YamlUtility.parseYaml(value);

        return nodes.properties().stream()
                .map(e2 -> Map.entry(
                        e2.getKey(),
                        Optional.ofNullable(e2.getValue().get("needs")).map(e -> e.isArray()
                                ? StreamSupport.stream(Spliterators.spliteratorUnknownSize(e.elements(), Spliterator.ORDERED), false).map(JsonNode::asText).collect(Collectors.toSet())
                                : Set.of(e.asText())).orElse(Collections.emptySet()))
                )
                .map(e -> new GraphUtility.JobRequirements(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());
    }
}
