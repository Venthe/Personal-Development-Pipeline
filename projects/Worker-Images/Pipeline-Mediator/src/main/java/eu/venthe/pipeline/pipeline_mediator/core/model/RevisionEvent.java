package eu.venthe.pipeline.pipeline_mediator.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.venthe.pipeline.pipeline_mediator.core.model.vo.ContinuousIntegrationEventStatus;
import eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding.GerritApi;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpEntity;

import static eu.venthe.pipeline.pipeline_mediator.dependencies.JenkinsUtilities.getByPath;

@Document(collection = "events")
@TypeAlias("revision@v1")
public class RevisionEvent extends AbstractContinuousIntegrationEvent {
    @SuppressWarnings("unused")
    @PersistenceCreator
    protected RevisionEvent(String referenceId, String originId, ObjectNode event, ContinuousIntegrationEventStatus status) {
        super(referenceId, originId, event, status);
    }

    private RevisionEvent(ObjectNode event) {
        super(event);
    }

    private RevisionEvent(String originId,
                          ObjectNode event) {
        super(originId, event);
    }

    public static RevisionEvent create(ObjectNode event,
                                       ObjectMapper objectMapper,
                                       GerritApi gerritApi) {
        HttpEntity<?> traceId = gerritApi.generateTraceId();

        ObjectNode root = objectMapper.createObjectNode();

        root.set("type", new TextNode(typeProvider(event)));

        ObjectNode metadata = objectMapper.createObjectNode();
        TextNode projectNameNode = getByPath(event, "refUpdate.project", JsonNode::asText).map(TextNode::new).orElseThrow();
        metadata.set("projectName", projectNameNode);
        TextNode revisionNode = getByPath(event, "refUpdate.newRev", JsonNode::asText).map(TextNode::new).orElseThrow();
        metadata.set("revision", revisionNode);
        metadata.set("branchName", getByPath(event, "refUpdate.refName", JsonNode::asText).map(TextNode::new).orElseThrow());
        root.set("metadata", metadata);

        ObjectNode additionalProperties = objectMapper.createObjectNode();
        additionalProperties.set("commit", gerritApi.getCommitForProject(projectNameNode.textValue(), revisionNode.textValue(), traceId));
        additionalProperties.set("files", gerritApi.getCommitFilesForProject(projectNameNode.textValue(), revisionNode.textValue(), traceId));
        root.set("additionalProperties", additionalProperties);

        return new RevisionEvent(root);
    }

    @Override
    public AbstractContinuousIntegrationEvent copyForRetrigger() {
        return new RevisionEvent(getReferenceId(), getEvent().deepCopy());
    }
}
