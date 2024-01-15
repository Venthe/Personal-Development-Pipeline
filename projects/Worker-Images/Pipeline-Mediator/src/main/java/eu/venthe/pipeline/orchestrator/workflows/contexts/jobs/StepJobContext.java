package eu.venthe.pipeline.orchestrator.workflows.contexts.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.workflows.contexts.EnvironmentContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts.OutputsContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts.StepsContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class StepJobContext {
    private final ObjectNode root;

    public static StepJobContext create(ObjectNode root) {
        return new StepJobContext(root);
    }

    public Optional<StepsContext> getSteps() {
        return StepsContext.create(root);
    }

    public Optional<EnvironmentContext> getEnv() {
        return EnvironmentContext.create(root);
    }

    public Integer getTimeoutMinutes() {
        return Optional.ofNullable(root.get("timeoutMinutes")).map(JsonNode::asInt).orElse(15);
    }

    public Boolean getContinueOnError() {
        return Optional.ofNullable(root.get("continueOnError")).map(JsonNode::asBoolean).orElse(false);
    }

    public Optional<OutputsContext> getOutputs() {
        return OutputsContext.create(root);
    }
}
