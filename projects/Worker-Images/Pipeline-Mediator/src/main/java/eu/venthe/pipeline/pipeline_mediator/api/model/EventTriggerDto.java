package eu.venthe.pipeline.pipeline_mediator.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Nullable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Value
@EqualsAndHashCode
public class EventTriggerDto {
    @Jacksonized
    @Builder(access = AccessLevel.PROTECTED)
    public EventTriggerDto(@NonNull String type,
                           @NonNull String projectName,
                           @Nullable String revision,
                           @Nullable String branchName,
                           @Nullable String originId,
                           @Nullable String workflow,
                           @Nullable Map<String, JsonNode> inputs) {
        this.type = type;
        this.projectName = projectName;
        this.revision = revision;
        this.branchName = branchName;
        this.originId = originId;
        this.workflow = workflow;
        this.inputs = inputs;

        if (this.branchName == null && this.revision == null) {
            throw new UnsupportedOperationException("You need to have at least a branch or a revision");
        }
    }

    @NonNull String type;
    @NonNull String projectName;
    @Nullable
    String revision;
    @Nullable
    String branchName;
    @Nullable
    String originId;
    @Nullable
    String workflow;
    @Nullable
    Map<String, JsonNode> inputs;
}
