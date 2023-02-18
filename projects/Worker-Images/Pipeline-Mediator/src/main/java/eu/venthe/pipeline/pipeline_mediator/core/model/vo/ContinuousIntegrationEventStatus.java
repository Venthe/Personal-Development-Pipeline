package eu.venthe.pipeline.pipeline_mediator.core.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

import static eu.venthe.pipeline.pipeline_mediator.core.model.vo.EventStatus.*;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ContinuousIntegrationEventStatus {

    @NonNull EventStatus status;
    @Nullable
    String reason;

    public static ContinuousIntegrationEventStatus initial() {
        return new ContinuousIntegrationEventStatus(REGISTERED_FOR_PROCESSING, null);
    }

    public boolean isAlreadyDelivered() {
        return getStatus().equals(PIPELINE_TRIGGERED);
    }

    public boolean isCompletedSuccessfully() {
        return getStatus().equals(PIPELINE_COMPLETED);
    }

    public void pipelineTriggered() {
        status = PIPELINE_TRIGGERED;
    }

    public void undeliverable() {
        status = UNDELIVERABLE;
    }

    public void markQueued() {
        status = REGISTERED_FOR_PROCESSING;
    }

    public void markAsErrorRegistering() {
        status = RETRY_REQUIRED;
    }

    public void markSkipped(String reason) {
        status = SKIPPED;
        this.reason = reason;
    }

    public void setStatus(EventStatus status) {
        if (this.status != PIPELINE_TRIGGERED) {
            throw new UnsupportedOperationException("Cannot set status %s, because current status is %s".formatted(status, this.status));
        }

        if (!List.of(PIPELINE_FAILED, PIPELINE_COMPLETED).contains(status)) {
            throw new UnsupportedOperationException("Cannot change for status %s".formatted(status));
        }

        this.status = status;
    }
}
