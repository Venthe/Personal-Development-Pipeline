/*
package eu.venthe.pipeline.orchestrator.scratch.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ExpressionUtilitiesTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("expressions")
    void test(String expression, String result, ObjectNode context) {
        Assertions.assertThat(ExpressionUtilities.evaluate(expression, context))
                .isEqualTo(result);
    }

    private final static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.of("", "", empty()),
                Arguments.of("${{simple}}", "Simple", simple()),
                Arguments.of("Works!", "Works!", empty())
        );
    }

    private static ObjectNode empty() {
        return objectMapper.createObjectNode();
    }

    private static ObjectNode simple() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("simple", objectMapper.getNodeFactory().textNode("Simple"));
        return objectNode;
    }
}
*/
