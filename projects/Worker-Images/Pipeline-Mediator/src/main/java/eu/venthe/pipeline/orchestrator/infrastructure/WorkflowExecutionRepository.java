package eu.venthe.pipeline.orchestrator.infrastructure;

import eu.venthe.pipeline.orchestrator.workflow_executions.WorkflowExecution;

import java.util.Optional;

public interface WorkflowExecutionRepository {
    void save(WorkflowExecution workflowExecution);

    Optional<WorkflowExecution> get(String workflowExecutionId);
}
