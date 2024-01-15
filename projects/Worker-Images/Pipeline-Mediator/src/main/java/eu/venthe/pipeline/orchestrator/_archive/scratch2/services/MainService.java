/*
package eu.venthe.pipeline.orchestrator.scratch2.services;

import eu.venthe.pipeline.orchestrator.scratch2.model.events.Event;
import eu.venthe.pipeline.orchestrator.scratch2.model.Workflow;
import eu.venthe.pipeline.orchestrator.scratch2.model.events.EventType;
import eu.venthe.pipeline.orchestrator.scratch2.model.events.interfaces.RemoteWorkflowCall;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {
    private final VersionControlSystem versionControlSystem;
    private final WorkflowExecutionRepository workflowExecutionRepository;

    public void handleEvent(Event event) {
        loadWorkflowsForEvent(event)
                .stream()
                .map(workflow -> workflow.toExecution(event))
                .flatMap(Optional::stream)
                .forEach(workflowExecution -> {
                    workflowExecution.execute();
                    workflowExecutionRepository.save(workflowExecution);
                });
    }

    private List<Workflow> loadWorkflowsForEvent(Event event) {
        if (List.of(EventType.WORKFLOW_DISPATCH, EventType.WORKFLOW_CALL).contains(event.getType())) {
            RemoteWorkflowCall mappedEvent = (RemoteWorkflowCall) event;
            return versionControlSystem.getFile(event.getRepositoryContext().getProjectName(), event.getRepositoryContext().getRef(), mappedEvent.getWorkflow())
                    .map(Workflow::fromFile)
                    .map(List::of)
                    .orElse(Collections.emptyList());
        }

        return versionControlSystem.getFiles(event.getRepositoryContext().getProjectName(), event.getRepositoryContext().getRef(), ".pipeline/workflows/  // .ya?ml").stream()
                .map(Workflow::fromFile)
                .toList();
    }

}
*/
