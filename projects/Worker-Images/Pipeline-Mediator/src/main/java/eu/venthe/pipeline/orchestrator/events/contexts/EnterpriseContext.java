package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnterpriseContext {
    private final ObjectNode root;

    public static Optional<EnterpriseContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "enterprise", EnterpriseContext::new);
    }
}
