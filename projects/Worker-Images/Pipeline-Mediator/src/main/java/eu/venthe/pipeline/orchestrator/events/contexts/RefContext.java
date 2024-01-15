package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RefContext {
    public static Optional<String> ref(ObjectNode root) {
        return Optional.ofNullable(root.get("ref"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText);
    }
}
