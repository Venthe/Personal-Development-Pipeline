package eu.venthe.pipeline.orchestrator.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextBranches;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextPaths;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextInputs;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextTypes;

import java.util.Map;

public interface Event extends BaseEvent {

    Map<String, ObjectNode> getNameContext();

    String getRunName();

    default Boolean matches(OnContextTypes types) {
        return true;
    }

    default Boolean matches(OnContextInputs inputs) {
        return true;
    }

    default Boolean matches(OnContextBranches onContextBranches) {
        return true;
    }

    default Boolean matches(OnContextPaths onContextPaths) {
        return true;
    }
}
