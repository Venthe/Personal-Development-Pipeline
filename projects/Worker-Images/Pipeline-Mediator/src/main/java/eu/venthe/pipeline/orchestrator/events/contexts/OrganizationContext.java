package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrganizationContext {
    private final ObjectNode root;

    public static Optional<OrganizationContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "organization", OrganizationContext::new);
    }
}
