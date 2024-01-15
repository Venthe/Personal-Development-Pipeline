package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SenderContext {
    private final ObjectNode event;

    public static Optional<SenderContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "sender", SenderContext::new);
    }
}
