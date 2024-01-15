/*
package eu.venthe.pipeline.orchestrator.archive.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.venthe.pipeline.orchestrator.archive.api.model.EventTriggerDto;
import eu.venthe.pipeline.orchestrator.archive.core.model.vo.ContinuousIntegrationEventStatus;
import eu.venthe.pipeline.orchestrator.archive.dependencies.gerrit_hook_binding.GerritApi;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpEntity;

@Document(collection = "events")
@TypeAlias("dispatch@v1")
@Getter(AccessLevel.PROTECTED)
public class DispatchEvent extends AbstractContinuousIntegrationEvent {
    @SuppressWarnings("unused")
    @PersistenceCreator
    protected DispatchEvent(String referenceId, String originId, ObjectNode event, ContinuousIntegrationEventStatus status) {
        super(referenceId, originId, event, status);
    }

    private DispatchEvent(ObjectNode event) {
        super(event);
    }

    private DispatchEvent(String originId,
                          ObjectNode event) {
        super(originId, event);
    }

    public static DispatchEvent create(EventTriggerDto trigger,
                                       ObjectMapper objectMapper,
                                       GerritApi gerritApi) {
        HttpEntity<?> traceId = gerritApi.generateTraceId();

        ObjectNode root = objectMapper.createObjectNode();

        root.set("type", new TextNode(trigger.getType()));

        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.set("projectName", new TextNode(trigger.getProjectName()));
        String revision = getRevision(trigger, gerritApi, traceId);
        metadata.set("revision", new TextNode(revision));
        metadata.set("branchName", new TextNode(getBranchName(trigger, revision)));
        root.set("metadata", metadata);

        ObjectNode additionalProperties = objectMapper.createObjectNode();
        additionalProperties.set("workflow", new TextNode(trigger.getWorkflow()));
        additionalProperties.set("inputs", objectMapper.valueToTree(trigger.getInputs()));
        additionalProperties.set("commit", gerritApi.getCommitForProject(trigger.getProjectName(), revision, traceId));
        additionalProperties.set("files", gerritApi.getCommitFilesForProject(trigger.getProjectName(), revision, traceId));
        root.set("additionalProperties", additionalProperties);

        return new DispatchEvent(root);
    }

    private static String getBranchName(EventTriggerDto trigger,
                                        String revision) {
        return trigger.getBranchName() == null
                ? revision
                : trigger.getBranchName();
    }

    private static String getRevision(EventTriggerDto trigger,
                                      GerritApi gerritApi,
                                      HttpEntity<?> traceId) {
        return trigger.getRevision() == null
                ? gerritApi.getRevisionForBranchOrRevision(trigger.getProjectName(), trigger.getBranchName(), traceId)
                : trigger.getRevision();
    }

    @Override
    public AbstractContinuousIntegrationEvent copyForRetrigger() {
        return new DispatchEvent(getReferenceId(), getEvent().deepCopy());
    }
}
*/
