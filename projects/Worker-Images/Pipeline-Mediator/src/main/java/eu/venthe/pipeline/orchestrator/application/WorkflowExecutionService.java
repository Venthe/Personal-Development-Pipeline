package eu.venthe.pipeline.orchestrator.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.BaseEvent;
import eu.venthe.pipeline.orchestrator.infrastructure.WorkflowExecutionRepository;
import eu.venthe.pipeline.orchestrator.workflow_executions.JobExecutionStatus;
import eu.venthe.pipeline.orchestrator.workflow_executions.WorkflowExecution;
import eu.venthe.pipeline.orchestrator.workflows.Workflow;
import eu.venthe.pipeline.orchestrator.workflows.contexts.EnvironmentContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.StepJobContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts.OutputsContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.contexts.StepsContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final ObjectMapper objectMapper;

    public void updateStatus(String workflowId, WorkflowUpdate workflowUpdate) {
        log.info("Received workflow update for {}", workflowId);
        WorkflowExecution workflowExecution = workflowExecutionRepository.get(workflowId).orElseThrow();

        workflowExecution.updateJob(workflowUpdate.getJobId(), workflowUpdate.getJobExecutionStatus());

        workflowExecutionRepository.save(workflowExecution);
    }

    public Optional<String> getEvent(String workflowExecutionId) {
        return workflowExecutionRepository.get(workflowExecutionId)
                .map(WorkflowExecution::getEvent)
                .map(BaseEvent::getRaw)
                .map(BaseJsonNode::toPrettyString);
    }

    public Optional<ObjectNode> getJobDefinition(String workflowExecutionId, String jobId) {
        return workflowExecutionRepository.get(workflowExecutionId).map(workflowExecution -> {
            Workflow workflow = workflowExecution.getWorkflow();
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.set("projectName", objectMapper.getNodeFactory().textNode("Example-Project"));
            objectNode.set("ref", objectMapper.getNodeFactory().textNode("refs/heads/main"));

            objectNode.set("workflow", objectMapper.getNodeFactory().textNode(workflow.getName()));
            objectNode.set("event", workflowExecution.getEvent().getRaw());

            StepJobContext stepJobContext = workflow.getJobs().getJob(jobId).specify(StepJobContext::create);

            ObjectNode env = objectMapper.createObjectNode();
            workflow.getEnv().map(EnvironmentContext::getProperties).ifPresent(e-> e.forEach(updateObjectNode(env)));
            stepJobContext.getEnv().map(EnvironmentContext::getProperties).ifPresent(e-> e.forEach(updateObjectNode(env)));

            objectNode.set("env", env);
            ObjectNode outputs = objectMapper.createObjectNode();
            stepJobContext.getOutputs().map(OutputsContext::getProperties).orElseGet(Collections::emptyMap).forEach(updateObjectNode(outputs));
            objectNode.set("outputs", outputs);
            objectNode.set("steps", stepJobContext.getSteps().map(StepsContext::getRaw).orElseGet(objectMapper::createArrayNode));
            objectNode.set("timeoutMinutes", objectMapper.getNodeFactory().numberNode(stepJobContext.getTimeoutMinutes()));
            objectNode.set("continueOnError", objectMapper.getNodeFactory().booleanNode(stepJobContext.getContinueOnError()));

            // Reusable workflow / downstream workflow
            objectNode.set("inputs", objectMapper.getNodeFactory().objectNode());
            return objectNode;
        });
    }

    @NotNull
    private BiConsumer<String, String> updateObjectNode(ObjectNode on) {
        return (k, v) -> on.set(k, objectMapper.getNodeFactory().textNode(v));
    }

    public void updateStatus(String workflowExecutionId, String jobId, JobExecutionStatus status) {
        WorkflowExecution workflowExecution = workflowExecutionRepository.get(workflowExecutionId).orElseThrow();
        workflowExecution.updateJob(jobId, status);
        workflowExecutionRepository.save(workflowExecution);
    }

    public void updateOutputs(String workflowExecutionId, String jobId, ObjectNode e) {
        log.debug("UPDATING OUTPUTS: {}, {}, {}", workflowExecutionId, jobId, e);
    }

    public void updateStepStatus(String workflowExecutionId, String jobId, String stepIndex, JobExecutionStatus of) {
        log.debug("UPDATING STEPS: {}, {}, {}, {}", workflowExecutionId, jobId, stepIndex, of);
    }

    @Value
    public static class WorkflowUpdate {
        String jobId;
        JobExecutionStatus jobExecutionStatus;
    }
}
