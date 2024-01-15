/*
package eu.venthe.pipeline.orchestrator.archive.core.repository;

import eu.venthe.pipeline.orchestrator.archive.core.model.AbstractContinuousIntegrationEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContinuousIntegrationEventCustomImpl implements ContinuousIntegrationEventCustom {

    private final MongoTemplate mongoTemplate;

    public Optional<AbstractContinuousIntegrationEvent> findByReferenceId(@NonNull String referenceId) {
        return Optional.ofNullable(mongoTemplate.findOne(Query.query(Criteria.where("referenceId").is(referenceId)), AbstractContinuousIntegrationEvent.class));
    }
}
*/
