package eu.venthe.pipeline.orchestrator.api;

import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.application.WorkflowExecutionService;
import eu.venthe.pipeline.orchestrator.workflow_executions.JobExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow-executions")
public class WorkflowExecutionHttpApi {
    private final WorkflowExecutionService workflowExecutionService;

    @GetMapping("{workflowExecutionId}/jobs/{jobId}")
    ResponseEntity<ObjectNode> getEvent(@PathVariable String workflowExecutionId, @PathVariable String jobId) {
        return ResponseEntity.of(workflowExecutionService.getJobDefinition(workflowExecutionId, jobId));
    }

    @PostMapping(value = "{workflowExecutionId}/jobs/{jobId}/update-status", consumes = {MediaType.APPLICATION_JSON_VALUE})
    void updateJobStatus(@PathVariable String workflowExecutionId, @PathVariable String jobId, @RequestBody ObjectNode data) {
        workflowExecutionService.updateStatus(workflowExecutionId, jobId, JobExecutionStatus.of(data.get("status").asText()));
        Optional.ofNullable(data.get("outputs")).map(ObjectNode.class::cast).ifPresent(e -> workflowExecutionService.updateOutputs(workflowExecutionId, jobId, e));

    }

    @PostMapping(value = "{workflowExecutionId}/jobs/{jobId}/steps/{stepIndex}/update-status", consumes = {MediaType.TEXT_PLAIN_VALUE, "text/plain;charset=UTF-8"})
    void updateStepStatus(@PathVariable String workflowExecutionId, @PathVariable String jobId, @PathVariable String stepIndex, @RequestBody String status) {
        workflowExecutionService.updateStepStatus(workflowExecutionId, jobId, stepIndex, JobExecutionStatus.of(status));
    }
}
