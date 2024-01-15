package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import eu.venthe.pipeline.orchestrator.utilities.GraphUtility;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.BaseJobContext;
import eu.venthe.pipeline.orchestrator.workflows.contexts.jobs.StepJobContext;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JobsContext {
    private final ObjectNode root;

    public static Optional<JobsContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "jobs", JobsContext::new);
    }

    public List<List<String>> getTree() {
        if(!root.isObject()) throw new RuntimeException();

        Set<GraphUtility.JobRequirements> jobRequirements = root.properties().stream()
                .map(e -> Map.entry(e.getKey(), BaseJobContext.create(e.getValue())))
                .map(e -> new GraphUtility.JobRequirements(e.getKey(), new HashSet<>(e.getValue().getNeeds())))
                .collect(Collectors.toSet());

        return GraphUtility.buildDependencyTree(jobRequirements);
    }

    public BaseJobContext getJob(String jobId) {
        return BaseJobContext.create(root.get(jobId));
    }
}
