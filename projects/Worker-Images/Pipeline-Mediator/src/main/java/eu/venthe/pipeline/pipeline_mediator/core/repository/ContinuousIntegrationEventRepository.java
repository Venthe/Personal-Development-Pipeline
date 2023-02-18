package eu.venthe.pipeline.pipeline_mediator.core.repository;

import eu.venthe.pipeline.pipeline_mediator.core.model.AbstractContinuousIntegrationEvent;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ContinuousIntegrationEventRepository extends MongoRepository<AbstractContinuousIntegrationEvent, ObjectId>, ContinuousIntegrationEventCustom {
}
