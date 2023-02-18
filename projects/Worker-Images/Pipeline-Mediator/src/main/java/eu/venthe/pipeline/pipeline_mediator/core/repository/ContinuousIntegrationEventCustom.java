package eu.venthe.pipeline.pipeline_mediator.core.repository;

import eu.venthe.pipeline.pipeline_mediator.core.model.AbstractContinuousIntegrationEvent;
import lombok.NonNull;

import java.util.Optional;

public interface ContinuousIntegrationEventCustom {
    Optional<AbstractContinuousIntegrationEvent> findByReferenceId(@NonNull String referenceId);
}
