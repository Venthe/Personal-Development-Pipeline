package eu.venthe.pipeline.orchestrator.workflow_executions;

import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.BaseJobContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

@Slf4j
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@Getter
public class JobState {
    private final String id;
    private ZonedDateTime startDate;
    private final BaseJobContext job;
    private JobExecutionStatus status = JobExecutionStatus.WAITING;
    private ZonedDateTime endTime;

    public static JobState initialize(String id, BaseJobContext context) {
        return new JobState(id, context);
    }

    public void queue(Consumer<String> execute) {
        startDate = ZonedDateTime.now();
        status = JobExecutionStatus.QUEUED;

        execute.accept(id);
    }

    public void setStatus(JobExecutionStatus jobExecutionStatus) {
        this.status = jobExecutionStatus;
    }
}
