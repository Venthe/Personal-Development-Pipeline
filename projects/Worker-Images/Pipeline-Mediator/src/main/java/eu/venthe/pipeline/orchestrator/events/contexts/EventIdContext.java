package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EventIdContext {
    public static Optional<UUID> id(ObjectNode root) {
        return Optional.ofNullable(root.get("id"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText)
                .map(UUID::fromString);
    }
}
