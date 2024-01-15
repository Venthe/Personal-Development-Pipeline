/*
package eu.venthe.pipeline.orchestrator.scratch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.scratch.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class WorkflowExecutionService {
    private final Executor executor;

    void run(Event event) {
        List<Workflow> workflows = event.isTriggeringSpecificWorkflow()
                ? List.of(this.getWorkflow(event))
                : this.provideAllWorkflows().stream().filter(workflow -> workflow.isApplicable(event)).toList();

        workflows.stream().map(Workflow::toWorkflowExecution).forEach(execution -> execution.execute(executor));

    }

    private List<Workflow> provideAllWorkflows() {
        return null;
    }

    private Workflow getWorkflow(Event event) {
        return null;
    }

    @RequiredArgsConstructor
    static class Workflow {
        private final ContextHelper contextHelper;

        private final Event event;
        private final GithubContext githubContext;
        private final String workflowFilePath;
        private final ObjectNode workflow;

        public String getName() {
            return ofNullable(workflow.get("name").asText())
                    .orElse(workflowFilePath);
        }

        public String getRunName() {
            return ofNullable(workflow.get("name").asText())
                    .map(expression -> ExpressionUtilities.evaluate(
                            expression,
                            contextHelper.toContext(Map.of(
                                    "github", githubContext.expose(),
                                    "inputs", null
                            ))
                    ))
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .orElse(event.getRunName());
        }

        public boolean isApplicable(Event event) {
            return false;
        }

        public WorkflowExecution toWorkflowExecution() {
            return null;
        }
    }

    @RequiredArgsConstructor
    static class GithubContext {
        private final ObjectNode context;

        public ObjectNode expose() {
            return context;
        }
    }

    @UtilityClass
    static class ExpressionUtilities {
        String evaluate(String expression, ObjectNode context) {
            // run-name: Deploy to ${{ inputs.deploy_target }} by @${{ github.actor }}
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor
    static class ContextHelper {
        private final ObjectMapper objectMapper;

        ObjectNode toContext(Map<String, ObjectNode> contextMap) {
            ObjectNode objectNode = objectMapper.createObjectNode();

            contextMap.forEach(objectNode::set);

            return objectNode;
        }
    }

    @RequiredArgsConstructor
    static class WorkflowExecution {
        public void execute(Executor executor) {

        }
    }

    @Component
    @RequiredArgsConstructor
    static class Executor {
    }
}
*/
