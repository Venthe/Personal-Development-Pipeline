package eu.venthe.pipeline.orchestrator.events;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.UUID;

public interface BaseEvent {
    EventType getType();

    UUID getId();

    ObjectNode getRaw();
}
