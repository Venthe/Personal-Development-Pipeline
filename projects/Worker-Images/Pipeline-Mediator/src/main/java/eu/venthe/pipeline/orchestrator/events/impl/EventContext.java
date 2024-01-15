package eu.venthe.pipeline.orchestrator.events.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.BaseEvent;
import eu.venthe.pipeline.orchestrator.events.EventType;
import eu.venthe.pipeline.orchestrator.events.contexts.EventIdContext;
import eu.venthe.pipeline.orchestrator.events.contexts.TypeContext;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.function.Function;

public class EventContext implements BaseEvent {
    protected final ObjectNode root;

    public EventContext(ObjectNode root) {
        this.root = root;

        if (TypeContext.type(root).isEmpty()) {
            throw new IllegalArgumentException("Type must be properly mapped");
        }

        if (EventIdContext.id(root).isEmpty()) {
            throw new IllegalArgumentException("Unique ID for the event must be present");
        }
    }

    public EventContext(ObjectNode root, EventType eventType) {
        this(root);

        if (!getType().equals(eventType)) {
            throw new IllegalArgumentException(MessageFormat.format("Unsupported event type {0}. Expected {1}", getType(), eventType));
        }
    }

    public <T> T specify(Function<ObjectNode, T> creator) {
        return creator.apply(root);
    }

    @Override
    public final EventType getType() {
        return TypeContext.type(root).orElseThrow();
    }

    @Override
    public final UUID getId() {
        return EventIdContext.id(root).orElseThrow();
    }

    @Override
    public final ObjectNode getRaw() {
        return root;
    }
}
