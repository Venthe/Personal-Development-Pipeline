package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RepositoryContext {
    private final ObjectNode root;

    public static Optional<RepositoryContext> create(ObjectNode root) {
        return ContextUtilities.get(root, "repository", RepositoryContext::new);
    }

    public String getProject() {
        return Optional.ofNullable(root.get("project"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText).orElseThrow();
    }

    public String getRepositoryId() {
        return Optional.ofNullable(root.get("repositoryId"))
                .filter(Predicate.not(JsonNode::isNull))
                .map(JsonNode::asText).orElseThrow();
    }
}
