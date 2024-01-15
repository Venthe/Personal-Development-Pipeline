package eu.venthe.pipeline.orchestrator.infrastructure;

public interface JobExecutor {
    void queueStepped(String workflowExecutionId, String jobId);
}
