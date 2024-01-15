package eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class StepsContext {
    private final ArrayNode root;

    public static Optional<StepsContext> create(JsonNode root) {

        JsonNode steps = root.get("steps");

        if(steps == null || steps.isNull()) {
            return Optional.empty();
        }

        if (!steps.isArray()) throw new IllegalArgumentException();

        return Optional.of(new StepsContext((ArrayNode) steps));
    }

    public ArrayNode getRaw() {
        return root;
    }
}
