package eu.venthe.pipeline.orchestrator.events.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.events.EventType;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextBranches;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextTypes;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class PullRequestEventContext extends EventContext implements Event {
    public PullRequestEventContext(ObjectNode root) {
        super(root, EventType.PULL_REQUEST);
    }

    @Override
    public Map<String, ObjectNode> getNameContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRunName() {
        throw new UnsupportedOperationException();
    }

    public String getAction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean matches(OnContextTypes types) {
      return OnHandlers.typesMatch(types, this, this::getAction);
    }
}
