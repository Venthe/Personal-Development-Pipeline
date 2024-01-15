package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class NameContext {

    public static Optional<String> name(ObjectNode root) {
        return Optional.ofNullable(root.get("name"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(TextNode.class::cast)
                .map(TextNode::asText);
    }
}
