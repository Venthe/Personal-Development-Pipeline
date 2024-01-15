/*
package eu.venthe.pipeline.orchestrator.scratch.workflows.model;

import eu.venthe.pipeline.orchestrator.scratch.events.Event;
import eu.venthe.pipeline.orchestrator.scratch.events.EventType;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkflowExecution {
    @EqualsAndHashCode.Include
    private final String id;
    private final Status status;
    private final Event event;
    private final String rootWorkflow;
    private final Map<String, Workflow> workflows;

    public String getId() {
        return id;
    }

//    public String getCorrelationId() {
//        return correlationId;
//    }
//
//    public String getRef() {
//        return ref;
//    }
//
//    public ZonedDateTime getStartDate() {
//        return startDate;
//    }
//
//    public Optional<ZonedDateTime> getEndDate() {
//        return endDate;
//    }
//
//    public EventType getEvent() {
//        return event;
//    }
//
//    public String getActor() {
//        return actor;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDescription() {
//        return description;
//    }

    public Status getStatus() {
        return status;
    }

//    public List<List<JobRun>> getJobs() {
//        return jobs;
//    }

    class JobRun {
        List<StepRun> steps;

        class StepRun {}
    }
}
*/
