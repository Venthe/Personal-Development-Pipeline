package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class ActionContext {
    public static Optional<String> ref(ObjectNode root) {
        return Optional.ofNullable(root.get("action"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText);
    }
}
