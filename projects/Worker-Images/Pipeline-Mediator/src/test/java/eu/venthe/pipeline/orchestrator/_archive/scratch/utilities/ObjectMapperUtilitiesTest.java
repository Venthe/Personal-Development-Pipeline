/*
package eu.venthe.pipeline.orchestrator.scratch.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static eu.venthe.pipeline.orchestrator.scratch.utilities.ObjectMapperUtilities.*;
import static org.assertj.core.api.Assertions.assertThat;


class ObjectMapperUtilitiesTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void name() {
        assertThat(transformToKeyValuePairs(objectMapper.createObjectNode()))
                .isEmpty();
    }

    @Test
    void name2() {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("simple", objectMapper.getNodeFactory().textNode("Simple!"));
        assertThat(transformToKeyValuePairs(objectNode))
                .containsEntry("simple", "Simple!");
    }
}
*/
