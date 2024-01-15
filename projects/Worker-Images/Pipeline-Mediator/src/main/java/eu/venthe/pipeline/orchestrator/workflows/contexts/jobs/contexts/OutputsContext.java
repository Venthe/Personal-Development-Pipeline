package eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OutputsContext {
    private final ObjectNode root;

    public static Optional<OutputsContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "outputs", OutputsContext::new);
    }

    public Map<String, String> getProperties() {
        return root.properties().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().asText()));
    }
}
