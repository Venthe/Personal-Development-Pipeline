package eu.venthe.pipeline.orchestrator.infrastructure;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.application.WorkflowExecutionService;
import eu.venthe.pipeline.orchestrator.workflow_executions.JobExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "jobExecutor", havingValue = "test", matchIfMissing = false)
public class TestJobExecutor implements JobExecutor {
    private final WorkflowExecutionService workflowExecutionService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void queueStepped(String workflowExecutionId, String jobId) {
        executorService.execute(() -> {
            try {
                log.info("TestJobExecutor - [{}][{}] Executing", workflowExecutionId, jobId);
                sleep(100);
                workflowExecutionService.updateStatus(workflowExecutionId, new WorkflowExecutionService.WorkflowUpdate(jobId, JobExecutionStatus.IN_PROGRESS));
                sleep(100);
                workflowExecutionService.updateStatus(workflowExecutionId, new WorkflowExecutionService.WorkflowUpdate(jobId, JobExecutionStatus.COMPLETED));

                log.info("TestJobExecutor - [{}][{}] Complete", workflowExecutionId, jobId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
