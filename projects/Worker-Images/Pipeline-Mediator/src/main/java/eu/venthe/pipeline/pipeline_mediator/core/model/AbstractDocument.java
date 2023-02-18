package eu.venthe.pipeline.pipeline_mediator.core.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

public abstract class AbstractDocument {
    @MongoId
    private ObjectId id;
    @CreatedDate
    private LocalDateTime createdDate;
    @Indexed
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @Version
    private Integer version;
}
