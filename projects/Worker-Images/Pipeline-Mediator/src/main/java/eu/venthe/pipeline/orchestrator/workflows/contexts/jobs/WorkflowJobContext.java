package eu.venthe.pipeline.orchestrator.workflows.contexts.jobs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkflowJobContext {
    private final ObjectNode root;

    public static WorkflowJobContext create(ObjectNode root) {
        return new WorkflowJobContext(root);
    }
}
