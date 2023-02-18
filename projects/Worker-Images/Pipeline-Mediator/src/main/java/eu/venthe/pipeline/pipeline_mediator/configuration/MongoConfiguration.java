package eu.venthe.pipeline.pipeline_mediator.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@EnableMongoAuditing
@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public MongoCustomConversions getMongoCustomConversions() {
        return new MongoCustomConversions(
                List.of(
                        new JsonSerializableToDocumentConverter(),
                        new DocumentToObjectNodeConverter(),
                        new DocumentToJsonNodeConverter()
                )
        );
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @WritingConverter
    private class JsonSerializableToDocumentConverter implements Converter<JsonSerializable.Base, Document> {
        @Override
        public Document convert(final JsonSerializable.Base source) {
            return Document.parse(source.toString());
        }
    }

    @ReadingConverter
    private class DocumentToJsonNodeConverter implements Converter<Document, JsonNode> {
        @Override
        public JsonNode convert(Document source) {
            try {
                return objectMapper.readTree(source.toJson());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to parse Document to JsonNode", e);
            }
        }
    }

    @ReadingConverter
    private class DocumentToObjectNodeConverter implements Converter<Document, ObjectNode> {
        @Override
        public ObjectNode convert(Document source) {
            try {
                JsonNode jsonNode = objectMapper.readTree(source.toJson());
                if (jsonNode.isArray()) {
                    throw new RuntimeException("Node is not an object" + jsonNode);
                }
                return (ObjectNode) jsonNode;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unable to parse Document to JsonNode", e);
            }
        }
    }
}
