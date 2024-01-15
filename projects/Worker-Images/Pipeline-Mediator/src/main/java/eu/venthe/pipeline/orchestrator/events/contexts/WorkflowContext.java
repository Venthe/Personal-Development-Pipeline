package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkflowContext {
    public static Optional<String> workflow(ObjectNode root) {
        return Optional.ofNullable(root.get("workflow"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText);
    }
}
