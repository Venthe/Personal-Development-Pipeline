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
@TypeAlias("change@v1")
public class ChangeEvent extends AbstractContinuousIntegrationEvent {
    @SuppressWarnings("unused")
    @PersistenceCreator
    protected ChangeEvent(String referenceId, String originId, ObjectNode event, ContinuousIntegrationEventStatus status) {
        super(referenceId, originId, event, status);
    }

    private ChangeEvent(ObjectNode event) {
        super(event);
    }

    private ChangeEvent(String originId,
                        ObjectNode event) {
        super(originId, event);
    }

    public static ChangeEvent create(ObjectNode event,
                                     ObjectMapper objectMapper,
                                     GerritApi gerritApi) {
        HttpEntity<?> traceId = gerritApi.generateTraceId();

        ObjectNode root = objectMapper.createObjectNode();

        root.set("type", new TextNode(typeProvider(event)));

        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.set("projectName", getByPath(event, "change.project", JsonNode::asText).map(TextNode::new).orElseThrow());
        TextNode revisionNode = getByPath(event, "patchSet.ref", JsonNode::asText).map(TextNode::new).orElseThrow();
        metadata.set("revision", revisionNode);
        metadata.set("branchName", getByPath(event, "patchSet.revision", JsonNode::asText).map(TextNode::new).orElseThrow());
        root.set("metadata", metadata);

        ObjectNode additionalProperties = objectMapper.createObjectNode();
        String changeId = getByPath(event, "change.id", JsonNode::asText).orElseThrow(() -> new UnsupportedOperationException("Change ID is required"));
        String revision = revisionNode.textValue();
        additionalProperties.set("commit", gerritApi.getCommitForRevision(changeId, revision, traceId));
        additionalProperties.set("files", gerritApi.getCommitFilesForRevision(changeId, revision, traceId));
        additionalProperties.set("change", getChange(gerritApi.getAllRevisions(changeId, traceId), revision));
        root.set("additionalProperties", additionalProperties);

        return new ChangeEvent(root);
    }

    private static ObjectNode getChange(JsonNode allRevisions, String revisionId) {
        JsonNode thisRevision = allRevisions.get("revisions").get(revisionId);
        ObjectNode change = allRevisions.deepCopy();
        change.remove("revisions");
        change.set("revision", thisRevision);
        return change;
    }

    @Override
    public AbstractContinuousIntegrationEvent copyForRetrigger() {
        return new ChangeEvent(getReferenceId(), getEvent().deepCopy());
    }
}
