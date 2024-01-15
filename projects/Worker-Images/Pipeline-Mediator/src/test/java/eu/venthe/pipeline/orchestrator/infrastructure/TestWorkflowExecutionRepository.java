package eu.venthe.pipeline.orchestrator.infrastructure;

import eu.venthe.pipeline.orchestrator.workflow_executions.WorkflowExecution;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class TestWorkflowExecutionRepository implements WorkflowExecutionRepository {
    private final Set<WorkflowExecution> data = new HashSet<>();

    @Override
    public void save(WorkflowExecution workflowExecution) {
        data.add(workflowExecution);
    }

    @Override
    public Optional<WorkflowExecution> get(String workflowExecutionId) {
        return data.stream()
                .filter(e -> e.getId().equals(workflowExecutionId))
                .findFirst();
    }
}
