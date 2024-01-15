package eu.venthe.pipeline.orchestrator.event_handlers;

import eu.venthe.pipeline.orchestrator.events.impl.EventContext;

import java.util.Optional;

public interface EventHandler {
    Optional<String> handle(EventContext event);
}
