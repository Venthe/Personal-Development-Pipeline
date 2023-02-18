package eu.venthe.pipeline.pipeline_mediator.core.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.pipeline_mediator.api.MediatorController;
import eu.venthe.pipeline.pipeline_mediator.api.model.EventTriggerDto;
import eu.venthe.pipeline.pipeline_mediator.core.EventFactory;
import eu.venthe.pipeline.pipeline_mediator.core.MediatorService;
import eu.venthe.pipeline.pipeline_mediator.core.repository.ContinuousIntegrationEventRepository;
import eu.venthe.pipeline.pipeline_mediator.dependencies.jenkins.JenkinsApi;
import eu.venthe.pipeline.pipeline_mediator.dependencies.transport.KafkaEventService;
import eu.venthe.pipeline.pipeline_mediator.dependencies.transport.QueuedEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Supplier;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class MediatorServiceImpl implements MediatorService {
    private final EventFactory eventFactory;
    private final ContinuousIntegrationEventRepository repository;
    private final KafkaEventService eventService;
    private final JenkinsApi jenkins;

    @Override
    public Optional<QueuedEvent> triggerManualEvent(@NonNull EventTriggerDto trigger) {
        var ciEvent = eventFactory.fromTrigger(trigger);
        try {
            return ciEvent.queue(eventService);
        } catch (Exception exception) {
            throw errorProcessingEvent(exception, ciEvent.getReferenceId());
        } finally {
            repository.save(ciEvent);
        }
    }

    @Override
    public Optional<QueuedEvent> handleGerritEvent(ObjectNode event) {
        return eventFactory.fromWebhook(event).flatMap(ciEvent -> {
            try {
                return ciEvent.queue(eventService);
            } catch (Exception exception) {
                throw errorProcessingEvent(exception, ciEvent.getReferenceId());
            } finally {
                repository.save(ciEvent);
            }
        });
    }

    @Override
    public Optional<QueuedEvent> retriggerEvent(String referenceId) {
        var ciEvent = repository.findByReferenceId(referenceId)
                .orElseThrow(referencedQueuedEventMustExist(referenceId));
        var newEvent = ciEvent.retrigger(eventService);
        repository.save(newEvent.getFirst());
        return newEvent.getSecond();
    }

    @Override
    public void changeStatus(MediatorController.Status payload) {
        var ciEvent = repository.findByReferenceId(payload.getReferenceId())
                .orElseThrow(referencedQueuedEventMustExist(payload.getReferenceId()));
        ciEvent.setStatus(payload.getStatus());
        repository.save(ciEvent);
    }

    @Override
    public void receiveQueuedEvent(QueuedEvent queuedEvent) {
        log.debug("Received queued event: {}", queuedEvent);
        var ciEvent = repository.findByReferenceId(queuedEvent.getReferenceId())
                .orElseThrow(referencedQueuedEventMustExist(queuedEvent.getReferenceId()));
        try {
            ciEvent.triggerPipeline(jenkins);
        } catch (Exception exception) {
            throw errorProcessingEvent(exception, queuedEvent.getReferenceId());
        } finally {
            repository.save(ciEvent);
        }
    }

    @Override
    public void registerFailedQueuedEvent(QueuedEvent queuedEvent) {
        log.debug("Received failed event: {}", queuedEvent);
        var ciEvent = repository.findByReferenceId(queuedEvent.getReferenceId())
                .orElseThrow(referencedQueuedEventMustExist(queuedEvent.getReferenceId()));
        ciEvent.markAsUndeliverable();
        repository.save(ciEvent);
    }

    @NotNull
    private static Supplier<RuntimeException> referencedQueuedEventMustExist(@NonNull String referenceId) {
        return () -> new RuntimeException("Event not found by reference id %s".formatted(referenceId));
    }

    @NotNull
    private static RuntimeException errorProcessingEvent(Exception exception, @NonNull String referenceId) {
        return new RuntimeException("There was a problem processing an event %s".formatted(referenceId), exception);
    }
}
