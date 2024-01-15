/*
package eu.venthe.pipeline.orchestrator.configuration;

import eu.venthe.pipeline.orchestrator.scratch.workflows.model.WorkflowExecution;
import eu.venthe.pipeline.orchestrator.scratch.workflows.ports.WorkflowExecutionRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class TestInMemoryWorkflowExecutionRepositoryImpl implements WorkflowExecutionRepository {
    private final Set<WorkflowExecution> workflowExecutions = new HashSet<>();

    @Override
    public Optional<WorkflowExecution> getByCorrelationId(String correlationId) {
        throw new UnsupportedOperationException();
//        return workflowExecutions.stream().filter(a -> a.getCorrelationId().equals(correlationId)).findFirst();
    }
}
*/
