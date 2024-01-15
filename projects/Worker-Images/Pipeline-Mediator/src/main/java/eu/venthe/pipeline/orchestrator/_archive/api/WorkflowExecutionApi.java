/*
package eu.venthe.pipeline.orchestrator.api;

import eu.venthe.pipeline.orchestrator.scratch.workflows.WorkflowExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionApi {
    private final WorkflowExecutionService workflowExecutionService;

    */
/**
     * @return Workflow execution DTO
     *//*

    public Optional<WorkflowExecutionDto> getForCorrelationId(String correlationId) {
        return workflowExecutionService.getByCorrelationId(correlationId).map(a -> {
           return new WorkflowExecutionDto(a.getStatus().getValue());
        });
    }

    @Value
    public static class WorkflowExecutionDto {
        String status;

        public List<List<String>> getJobs() {
            throw new UnsupportedOperationException();
        }
    }
}
*/
