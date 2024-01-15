/*
package eu.venthe.pipeline.orchestrator.scratch2.model;

import eu.venthe.pipeline.orchestrator.scratch2.model.vo.ExecutionId;
import eu.venthe.pipeline.orchestrator.scratch2.model.vo.JobId;
import eu.venthe.pipeline.orchestrator.scratch2.model.vo.WorkflowId;
import eu.venthe.pipeline.orchestrator.scratch2.services.ExecutorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
public class JobExecution {
    @Getter
    private final JobId id;
    private final WorkflowId workflowId;
    private final List<SingleExecution> executions;

    private final ExecutorService executorService;

    */
/**
     * Checks all executions and returns the most important one
     *
     * Cancelled > Paused > Error > Waiting > Running
     *//*

    public JobStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    public JobExecutionId execute() {
        throw new UnsupportedOperationException();
        */
/*if (jobIsAlreadyRunning()) {
            return getRunningJobExecutionId();
        }

        new SingleExecution(workflowId, id);*//*

    }

    public boolean isRunning() {
        throw new UnsupportedOperationException();
    }

    public void updateStatus(JobStatus jobStatus) {
        throw new UnsupportedOperationException();
    }

    public enum JobStatus {
    }

    @RequiredArgsConstructor
    public class SingleExecution {
        private final ExecutionId id;
        private final ZonedDateTime startDate;

        private JobStatus status;
        private ZonedDateTime endDate;
    }

    @RequiredArgsConstructor
    public static class JobExecutionId {
        private final JobExecution jobExecution;
        private final ExecutionId executionId;
    }
}
*/
