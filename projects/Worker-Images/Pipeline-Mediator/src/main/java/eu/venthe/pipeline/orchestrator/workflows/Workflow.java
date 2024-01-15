package eu.venthe.pipeline.orchestrator.workflows;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.BaseEvent;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.workflows.contexts.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class Workflow {

    private final ObjectNode root;
    @Getter
    private final String path;
    @Getter
    private final OnContext onContext;
    private JobsContext jobsContext;

    public Workflow(ObjectNode root, String path) {
        if (root == null) throw new IllegalArgumentException("Workflow should not be null");
        if (path == null) throw new IllegalArgumentException();

        this.root = root;
        this.path = path;

        onContext = OnContext.create(root)
                .orElseThrow(() -> new RuntimeException("There is no \"on\" property, this workflow will never run"));
        jobsContext = JobsContext.create(root).orElseThrow();
    }

    /**
     * The name of the workflow. If you omit name, GitHub displays the workflow file path relative to the root of the repository.
     */
    public String getName() {
        return NameContext.name(root).orElse(path);
    }

    public Optional<String> getRunName() {
        return RunNameContext.runName(root);
    }

    public Optional<PermissionsContext> getPermissions() {
        return PermissionsContext.create(root);
    }

    public Optional<EnvironmentContext> getEnv() {
        return EnvironmentContext.create(root);
    }

    public Optional<DefaultsContext> getDefaults() {
        return DefaultsContext.create(root);
    }

    public Optional<ConcurrencyContext> getConcurrency() {
        return ConcurrencyContext.create(root);
    }

    public JobsContext getJobs() {
        return jobsContext;
    }

    public Boolean on(Event event) {

        Boolean result = getOnContext().on(event);
        log.info("[id:{}][type:{}] Event match: {}", event.getId(), event.getType(), result);

        return result;
    }
}
