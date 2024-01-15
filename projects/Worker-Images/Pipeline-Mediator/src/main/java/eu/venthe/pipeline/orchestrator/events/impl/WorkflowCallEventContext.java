package eu.venthe.pipeline.orchestrator.events.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.events.EventType;

import java.util.Map;

public class WorkflowCallEventContext extends EventContext implements Event {
    public WorkflowCallEventContext(ObjectNode root) {
        super(root, EventType.WORKFLOW_CALL);
    }

    @Override
    public Map<String, ObjectNode> getNameContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRunName() {
        throw new UnsupportedOperationException();
    }
}
