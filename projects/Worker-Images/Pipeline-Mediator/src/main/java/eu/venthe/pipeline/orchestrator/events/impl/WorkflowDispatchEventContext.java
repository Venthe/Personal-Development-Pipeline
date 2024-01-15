package eu.venthe.pipeline.orchestrator.events.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.events.EventType;
import eu.venthe.pipeline.orchestrator.events.contexts.*;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextInputs;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
public class WorkflowDispatchEventContext extends EventContext implements Event {

    public WorkflowDispatchEventContext(ObjectNode root) {
        super(root, EventType.WORKFLOW_DISPATCH);
    }

    public Optional<InputsContext> getInputs() {
        return InputsContext.create(root);
    }

    public Optional<OrganizationContext> getOrganization() {
        return OrganizationContext.create(root);
    }

    public Optional<InstallationContext> getInstallation() {
        return InstallationContext.create(root);
    }

    public Optional<EnterpriseContext> getEnterpriseContext() {
        return EnterpriseContext.create(root);
    }

    public RepositoryContext getRepository() {
        return RepositoryContext.create(root).orElseThrow();
    }

    public SenderContext getSender() {
        return SenderContext.create(root).orElseThrow();
    }

    public String getWorkflow() {
        return WorkflowContext.workflow(root).orElseThrow();
    }

    public String getRef() {
        return RefContext.ref(root).orElseThrow();
    }

    public Map<String, ObjectNode> getNameContext() {
        return getInputs().map(InputsContext::getContext)
                .orElse(Map.of());
    }

    public String getRunName() {
        return "Workflow dispatch for " + getWorkflow();
    }

    public Boolean matches(OnContextInputs inputs) {
        return OnHandlers.inputsMatch(inputs, this, () -> getInputs().map(InputsContext::getInputKeys).orElse(Collections.emptyList()));
    }
}
