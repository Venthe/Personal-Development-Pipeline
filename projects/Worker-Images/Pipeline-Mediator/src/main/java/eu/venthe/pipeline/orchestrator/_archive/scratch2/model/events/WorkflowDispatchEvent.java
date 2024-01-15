/*
package eu.venthe.pipeline.orchestrator.scratch2.model.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.scratch2.model.events.contexts.*;
import eu.venthe.pipeline.orchestrator.scratch2.model.events.interfaces.RemoteWorkflowCall;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.text.MessageFormat.*;

public class WorkflowDispatchEvent extends Event implements RemoteWorkflowCall {

    public static Builder builder(ObjectMapper objectMapper) {
        return new Builder(objectMapper);
    }

    public WorkflowDispatchEvent(ObjectMapper objectMapper, ObjectNode event) {
        super(objectMapper, event);
    }

    public String getRunName() {
        return format("Workflow dispatch: {0}", getRepositoryContext().getRef());
    }

    public Optional<EnterpriseContext> getEnterprise() {
        return EnterpriseContext.create(event);
    }

    public InputsContext getInputs() {
        return InputsContext.create(event);
    }

    public Optional<InstallationContext> getInstallation() {
        return InstallationContext.create(event);
    }

    public Optional<OrganizationContext> getOrganization() {
        return OrganizationContext.create(event);
    }

    public SenderContext getSender() {
        return SenderContext.create(event).orElseThrow();
    }

    public String getWorkflow() {
        return WorkflowContext.create(event).orElseThrow();
    }

    public static class Builder {
        private final ObjectMapper objectMapper;
        ObjectNode event;

        public Builder(ObjectMapper objectMapper) {

            this.objectMapper = objectMapper;

            this.event = objectMapper.createObjectNode();
            this.event.set("type", objectMapper.getNodeFactory().textNode(EventType.WORKFLOW_DISPATCH.getValue()));
        }

        public WorkflowDispatchEvent build() {
            return new WorkflowDispatchEvent(objectMapper, this.event);
        }

        public Builder workflow(String workflow) {
            event.set("workflow", objectMapper.getNodeFactory().textNode(workflow));
            return this;
        }

        public Builder withRepository(UnaryOperator<RepositoryContext.Builder> repositoryContext) {
            event.set("repository", repositoryContext.apply(new RepositoryContext.Builder(objectMapper)).build().raw());

            return this;
        }

        public Builder ref(String xyz) {
            event.set("ref", objectMapper.getNodeFactory().textNode(xyz));
            return this;
        }
    }
}
*/
