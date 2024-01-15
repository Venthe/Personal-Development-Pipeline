/*
package eu.venthe.pipeline.orchestrator.archive.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.venthe.pipeline.orchestrator.archive.core.model.vo.ContinuousIntegrationEventStatus;
import eu.venthe.pipeline.orchestrator.archive.core.model.vo.EventStatus;
import eu.venthe.pipeline.orchestrator.archive.dependencies.JenkinsUtilities;
import eu.venthe.pipeline.orchestrator.archive.dependencies.jenkins.JenkinsApi;
import eu.venthe.pipeline.orchestrator.archive.dependencies.transport.KafkaEventService;
import eu.venthe.pipeline.orchestrator.archive.dependencies.transport.QueuedEvent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Document(collection = "events")
@AllArgsConstructor(onConstructor_ = @PersistenceCreator, access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class AbstractContinuousIntegrationEvent extends AbstractDocument {

    @Indexed
    @EqualsAndHashCode.Include
    @NonNull
    @Getter
    private final String referenceId;
    private final String originId;
    private final ObjectNode event;

    @NonNull
    private ContinuousIntegrationEventStatus status;

    protected final ObjectNode getEvent() {
        var metadata = (ObjectNode) event.get("metadata");
        metadata.set("referenceId", new TextNode(referenceId));
        metadata.set("originId", new TextNode(originId));
        event.set("metadata", metadata);
        return event;
    }

    protected AbstractContinuousIntegrationEvent(ObjectNode event) {
        this(null, event);
    }

    protected AbstractContinuousIntegrationEvent(String originId, ObjectNode event) {
        this(UUID.randomUUID().toString(), originId, event, ContinuousIntegrationEventStatus.initial());
    }

    public final QueuedEvent toQueuedEvent() {
        return new QueuedEvent(referenceId);
    }

    public final Pair<AbstractContinuousIntegrationEvent, Optional<QueuedEvent>> retrigger(KafkaEventService eventService) {
        log.info("Event retriggered {}", referenceId);
        var copy = copyForRetrigger();
        var queue = copy.queue(eventService);
        return Pair.of(copy, queue);
    }

    public final void triggerPipeline(JenkinsApi jenkinsApi) {
        if (!requiresProcessing()) {
            log.debug("Event already handled. {}", referenceId);
            return;
        }
        try {
            jenkinsApi.triggerPipeline(getEvent());
            status.pipelineTriggered();
            log.debug("Pipeline triggered for {}", referenceId);
        } catch (Exception e) {
            status.markAsErrorRegistering();
            log.warn("Retry required for {}", referenceId);
            throw new RuntimeException(e);
        }
    }

    public final void markAsUndeliverable() {
        status.undeliverable();
        log.info("Message undeliverable: {}", referenceId);
    }

    public final Optional<QueuedEvent> queue(KafkaEventService eventService) {
        if (JenkinsUtilities.getByPath(getEvent(), "metadata.branchName", JsonNode::asText)
                .filter(branchName -> branchName.endsWith("/meta") || branchName.startsWith("refs/users"))
                .isPresent()) {
            status.markSkipped("Internal gerrit meta branch");
            return Optional.empty();
        }

        try {
            status.markQueued();
            QueuedEvent queuedEvent = toQueuedEvent();
            eventService.send(queuedEvent);
            log.info("Queued event: {}", referenceId);
            return Optional.of(queuedEvent);
        } catch (Exception e) {
            status.markAsErrorRegistering();
            log.warn("Retry required for {}", referenceId);
            throw new RuntimeException(e);
        }
    }

    private final boolean requiresProcessing() {
        return !(status.isAlreadyDelivered() || status.isCompletedSuccessfully());
    }

    public abstract AbstractContinuousIntegrationEvent copyForRetrigger();

    public static String typeProvider(ObjectNode root) {
        return JenkinsUtilities.getByPath(root, "type", JsonNode::asText)
                .orElseThrow(() -> new RuntimeException("Type is required for an event"));
    }

    public void setStatus(EventStatus status) {
        try {
            getStatus().setStatus(status);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error setting status for %s".formatted(referenceId), e);
        }
    }
}
*/
