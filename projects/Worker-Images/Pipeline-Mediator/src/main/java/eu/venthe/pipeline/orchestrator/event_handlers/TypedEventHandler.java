package eu.venthe.pipeline.orchestrator.event_handlers;

import eu.venthe.pipeline.orchestrator.events.EventType;

public interface TypedEventHandler extends EventHandler {
    EventType discriminator();
}
