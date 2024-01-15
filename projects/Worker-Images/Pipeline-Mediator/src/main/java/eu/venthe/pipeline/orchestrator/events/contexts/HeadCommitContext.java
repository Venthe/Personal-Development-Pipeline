package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public class HeadCommitContext {
    public static Optional<HeadCommitContext> create(ObjectNode root) {
        throw new UnsupportedOperationException();
    }
}
