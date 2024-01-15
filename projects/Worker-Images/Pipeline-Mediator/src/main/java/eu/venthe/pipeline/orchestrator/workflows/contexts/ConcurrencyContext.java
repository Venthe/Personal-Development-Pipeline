package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ConcurrencyContext {
    private final ObjectNode root;

    public static Optional<ConcurrencyContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "concurrency", ConcurrencyContext::new);
    }
}
