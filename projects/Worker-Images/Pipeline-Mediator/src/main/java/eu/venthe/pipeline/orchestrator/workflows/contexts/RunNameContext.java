package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Optional;

import static java.util.function.Predicate.not;

public class RunNameContext {

    public static Optional<String> runName(ObjectNode root) {
        return Optional.ofNullable(root.get("runName"))
                .filter(not(JsonNode::isNull))
                .map(TextNode.class::cast)
                .map(TextNode::asText)
                .filter(not(String::isBlank));
    }
}
