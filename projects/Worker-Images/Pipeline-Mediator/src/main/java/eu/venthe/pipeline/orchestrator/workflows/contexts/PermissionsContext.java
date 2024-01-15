package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.contexts.RepositoryContext;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class PermissionsContext {
    private final ObjectNode root;

    public static Optional<PermissionsContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "permissions", PermissionsContext::new);
    }
}
