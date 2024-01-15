/*
package eu.venthe.pipeline.orchestrator.scratch;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.pipeline.orchestrator.configuration.MockRepository;
import eu.venthe.pipeline.orchestrator.scratch.api.EventApi;
import eu.venthe.pipeline.orchestrator.api.JobApi;
import eu.venthe.pipeline.orchestrator.api.WorkflowExecutionApi;
import eu.venthe.pipeline.orchestrator.scratch.workflows.model.Workflow;
import eu.venthe.pipeline.orchestrator.scratch.workflows.ports.VersionControlSystem;
import eu.venthe.pipeline.orchestrator.scratch.events.Event;
import eu.venthe.pipeline.orchestrator.scratch.events.EventType;
import eu.venthe.pipeline.orchestrator.scratch.events.WorkflowDispatchEvent;
import eu.venthe.pipeline.orchestrator.scratch.workflows.model.WorkflowExecution;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.venthe.pipeline.orchestrator.api.JobApi.*;
import static eu.venthe.pipeline.orchestrator.api.WorkflowExecutionApi.*;

@SpringBootTest
@TestPropertySource(properties = {
        "logging.level.eu.venthe=DEBUG"
})
@ExtendWith(MockitoExtension.class)
class SystemTest {
    private static final String SAMPLE_PROJECT = "SAMPLE_PROJECT";
    @MockBean
    VersionControlSystem versionControlSystem;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EventApi eventApi;
    @Autowired
    WorkflowExecutionApi workflowExecutionApi;
    @Autowired
    private JobApi jobApi;

    private String eventId;
    private WorkflowExecutionDto workflowExecutionDto;

    @Test
    void name() {
        // Given
        final WorkflowDispatchEvent workflowDispatch = getWorkflowDispatchEvent();
        final Workflow workflow = getWorkflowDispatchableWorkflow();
        setupSampleRepository((MockRepository repository) -> {
            repository.addWorkflow(workflow, workflowDispatch.getWorkflow());
        });

        systemReceivesEvent(workflowDispatch);
        workflowExecutionIsCreated();
        waitUntilJobsAreDone();
        workflowIsFinished();
    }

    @NotNull
    private WorkflowDispatchEvent getWorkflowDispatchEvent() {
        return WorkflowDispatchEvent.builder(objectMapper)
                .workflow("Sample workflow.yml")
                .withRepository(repository ->
                        repository.projectName(SAMPLE_PROJECT)
                )
                .ref("xyz")
                .build();
    }

    private Workflow getWorkflowDispatchableWorkflow() {
        return Workflow.builder(objectMapper, "Test")
                .on(EventType.WORKFLOW_DISPATCH)
                .build();
    }

    private void setupSampleRepository(Consumer<MockRepository> o) {
        o.accept(new MockRepository(SAMPLE_PROJECT, this.versionControlSystem));
    }

    private void workflowExecutionIsCreated() {
        Awaitility.await().with()
                .pollInterval(Duration.ofSeconds(1))
                .ignoreException(UnsupportedOperationException.class)
                .until(() -> {
            Optional<WorkflowExecutionDto> workflowExecutionDto = this.workflowExecutionApi.getForCorrelationId(this.eventId);
            workflowExecutionDto.ifPresent(wed -> {
                List<String> completed = Stream.of(
                        WorkflowExecution.Status.COMPLETED,
                        WorkflowExecution.Status.QUEUED,
                        WorkflowExecution.Status.WAITING,
                        WorkflowExecution.Status.IN_PROGRESS
                )
                        .map(WorkflowExecution.Status::getValue)
                        .collect(Collectors.toList());
                Assertions.assertThat(wed.getStatus()).describedAs(MessageFormat.format("Incorrect status for the workflow {0}", wed)).isIn(completed);
                this.workflowExecutionDto = wed;
            });
            return workflowExecutionDto.isPresent();
        });
    }

    private void waitUntilJobsAreDone() {
        List<List<String>> jobs = this.workflowExecutionDto.getJobs();
        Awaitility.await().with().pollInterval(Duration.ofSeconds(1)).until(() -> {
            Set<JobDto> allCompleted = jobs.stream().flatMap(Collection::stream)
                    .map(jobId -> this.jobApi.getJobById(jobId))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
            Assertions.assertThat(allCompleted.stream().map(JobDto::getStatus))
                    .describedAs(MessageFormat.format("Incorrect status for a job {0}", allCompleted))
                    .containsAnyOf(JobDto.Status.PENDING, JobDto.Status.COMPLETED);

            return allCompleted.stream().map(JobDto::getStatus).allMatch(e-> e.equals(JobDto.Status.COMPLETED));
        });
    }

    private void workflowIsFinished() {
        WorkflowExecutionDto workflowExecutionDto1 = this.workflowExecutionApi.getForCorrelationId(this.eventId).orElseThrow();
        Assertions.assertThat(workflowExecutionDto1.getStatus())
                .describedAs(MessageFormat.format("Incorrect status for a workflow execution. {0}", workflowExecutionDto1))
                .isEqualTo(WorkflowExecution.Status.COMPLETED.getValue());
    }

    private void systemReceivesEvent(Event workflowDispatch) {
        this.eventId = this.eventApi.handleEvent(workflowDispatch.getRaw());
    }
}
*/
