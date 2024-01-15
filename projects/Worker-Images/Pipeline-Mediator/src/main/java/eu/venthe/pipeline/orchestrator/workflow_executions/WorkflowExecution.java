package eu.venthe.pipeline.orchestrator.workflow_executions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.MoreCollectors;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.infrastructure.JobExecutor;
import eu.venthe.pipeline.orchestrator.utilities.ExpressionUtilities;
import eu.venthe.pipeline.orchestrator.workflows.Workflow;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.UnaryOperator;

import static eu.venthe.pipeline.orchestrator.workflow_executions.JobExecutionStatus.*;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class WorkflowExecution {
    @EqualsAndHashCode.Include
    private final String id = UUID.randomUUID().toString();
    private final Event event;
    private final Workflow workflow;

    private List<Map<String, JobState>> jobStates = new ArrayList<>();

    public static Optional<WorkflowExecution> from(Workflow workflow, Event event) {
        boolean shouldCreateWorkflowExecution = workflow.on(event);
        if (!shouldCreateWorkflowExecution) return Optional.empty();

        return Optional.of(new WorkflowExecution(event, workflow));
    }

    public String getRunName(ExpressionUtilities expressionUtilities) {
        Map<String, ObjectNode> nameContext = event.getNameContext();
        Map<String, ObjectNode> github = Map.of("github", getGithubContext());

        HashMap<String, JsonNode> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.putAll(nameContext);
        objectObjectHashMap.putAll(github);

        return workflow.getRunName()
                .map(workflowRunName -> expressionUtilities.evaluateOn(workflowRunName, objectObjectHashMap))
                .orElse(event.getRunName());
    }

    private ObjectNode getGithubContext() {
        throw new UnsupportedOperationException();
    }

    public void start(JobExecutor jobExecutor) {
        if (!jobStates.isEmpty()) {
            log.info("Workflow execution already started");
            return;
        }

        log.info("Running workflow execution {}", this);

        List<List<String>> jobOrder = workflow.getJobs().getTree();

        jobOrder.forEach(jobSet -> {
            jobStates.add(jobSet.stream().collect(toMap(UnaryOperator.identity(), jobId -> JobState.initialize(jobId, workflow.getJobs().getJob(jobId)))));
        });

        log.info("Prepared dependency tree {}", jobStates);
        jobStates.get(0).values()
                .forEach(state -> state.queue(jobId -> {
                    log.info("[{}][{}] Executing", getId(), jobId);
                    jobExecutor.queueStepped(getId(), jobId);
                }));
    }

    public WorkflowExecutionStatus getStatus() {
        Set<JobExecutionStatus> collect = jobStates.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(JobState::getStatus)
                .collect(toSet());

        if (collect.stream().anyMatch(CANCELLED::equals)) {
            return WorkflowExecutionStatus.CANCELLED;
        }

        if (collect.stream().anyMatch(FAILURE::equals)) {
            return WorkflowExecutionStatus.FAILURE;
        }

        if (collect.stream().anyMatch(e -> Set.of(QUEUED, IN_PROGRESS, WAITING).contains(e))) {
            return WorkflowExecutionStatus.IN_PROGRESS;
        }

        if (Set.of(COMPLETED, SUCCESS, SKIPPED, NEUTRAL).containsAll(collect)) {
            return WorkflowExecutionStatus.COMPLETED;
        }

        throw new UnsupportedOperationException();
    }

    public synchronized void updateJob(String jobId, JobExecutionStatus jobExecutionStatus) {
        log.info("Updating job {} for workflow {} to {}", jobId, getId(), jobExecutionStatus);
        jobStates.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(e -> e.getId().equals(jobId))
                .collect(MoreCollectors.toOptional())
                .orElseThrow()
                .setStatus(jobExecutionStatus);
    }
}
