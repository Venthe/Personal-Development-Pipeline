package eu.venthe.pipeline.pipeline_mediator.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.pipeline_mediator.api.MediatorController;
import eu.venthe.pipeline.pipeline_mediator.api.model.EventTriggerDto;
import eu.venthe.pipeline.pipeline_mediator.dependencies.transport.QueuedEvent;
import lombok.NonNull;

import java.util.Optional;

public interface MediatorService {
    Optional<QueuedEvent> triggerManualEvent(@NonNull EventTriggerDto trigger);

    Optional<QueuedEvent> handleGerritEvent(ObjectNode event);

    void receiveQueuedEvent(QueuedEvent event);

    void registerFailedQueuedEvent(QueuedEvent event);

    Optional<QueuedEvent> retriggerEvent(String referenceId);

    void changeStatus(MediatorController.Status payload);
}
