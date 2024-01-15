/*
package eu.venthe.pipeline.orchestrator.scratch.workflows.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.scratch.utilities.ExpressionUtilities;
import eu.venthe.pipeline.orchestrator.scratch.workflows.model.Workflow;
import eu.venthe.pipeline.orchestrator.scratch.workflows.model.WorkflowExecution;
import eu.venthe.pipeline.orchestrator.scratch.workflows.ports.ExecutorService;
import eu.venthe.pipeline.orchestrator.scratch.workflows.ports.VersionControlSystem;
import eu.venthe.pipeline.orchestrator.scratch.events.EventType;
import eu.venthe.pipeline.orchestrator.scratch.events.WorkflowDispatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowDispatchHandler implements EventHandler {
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private final VersionControlSystem versionControlSystem;
    private final ExpressionUtilities expressionUtilities;

    @Override
    public EventType getDiscriminator() {
        return EventType.WORKFLOW_DISPATCH;
    }

    @SneakyThrows
    @Override
    public void handle(ObjectNode event) {
        log.debug("Workflow dispatch handler {}", event);
        WorkflowDispatchEvent mappedEvent = new WorkflowDispatchEvent(objectMapper, event);

        Workflow workflow = getWorkflow(mappedEvent.getRepository().getProjectName(), mappedEvent.getRef(), mappedEvent.getWorkflow())
                .orElseThrow();

//        new WorkflowExecution()

        executorService.execute("123");
    }

    private Optional<Workflow> getWorkflow(String projectName, String ref, String workflowFileName) throws JsonProcessingException {
        Path workflowPath = Path.of(".pipeline", "workflows", workflowFileName);
        return Optional.ofNullable((ObjectNode) objectMapper.readTree(versionControlSystem.showFile(projectName, ref, workflowPath.toString()).orElseThrow()))
                .map((ObjectNode workflowFile) -> new Workflow(workflowFile, workflowFileName, expressionUtilities));
    }

}
*/
