package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EnvironmentContext {
    private final ObjectNode root;

    public static Optional<EnvironmentContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "env", EnvironmentContext::new);
    }

    public Map<String, String> getProperties() {
        return root.properties().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().asText()));
    }
}
