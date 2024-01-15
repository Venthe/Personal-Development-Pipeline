/*
package eu.venthe.pipeline.orchestrator.archive.core.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ContinuousIntegrationEventStatus {

    @NonNull EventStatus status;
    @Nullable
    String reason;

    public static ContinuousIntegrationEventStatus initial() {
        return new ContinuousIntegrationEventStatus(EventStatus.REGISTERED_FOR_PROCESSING, null);
    }

    public boolean isAlreadyDelivered() {
        return getStatus().equals(EventStatus.PIPELINE_TRIGGERED);
    }

    public boolean isCompletedSuccessfully() {
        return getStatus().equals(EventStatus.PIPELINE_COMPLETED);
    }

    public void pipelineTriggered() {
        status = EventStatus.PIPELINE_TRIGGERED;
    }

    public void undeliverable() {
        status = EventStatus.UNDELIVERABLE;
    }

    public void markQueued() {
        status = EventStatus.REGISTERED_FOR_PROCESSING;
    }

    public void markAsErrorRegistering() {
        status = EventStatus.RETRY_REQUIRED;
    }

    public void markSkipped(String reason) {
        status = EventStatus.SKIPPED;
        this.reason = reason;
    }

    public void setStatus(EventStatus status) {
        if (this.status != EventStatus.PIPELINE_TRIGGERED) {
            throw new UnsupportedOperationException("Cannot set status %s, because current status is %s".formatted(status, this.status));
        }

        if (!List.of(EventStatus.PIPELINE_FAILED, EventStatus.PIPELINE_COMPLETED).contains(status)) {
            throw new UnsupportedOperationException("Cannot change for status %s".formatted(status));
        }

        this.status = status;
    }
}
*/
