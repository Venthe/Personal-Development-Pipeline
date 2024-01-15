package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.CollectionUtilities;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OnContextPaths {
    private final ObjectNode root;

    public static OnContextPaths create(ObjectNode root) {
        return new OnContextPaths(root);
    }

    public List<String> files() {
        throw new UnsupportedOperationException();
    }

    public List<String> paths() {
        return params("pats");
    }

    public List<String> pathsIgnore() {
        return params("pathsIgnore");
    }

    @NotNull
    private List<String> params(String propertyName) {
        return Optional.ofNullable(root.get(propertyName))
                .map(JsonNode::elements)
                .map(elements -> CollectionUtilities.iteratorToStream(elements)
                        .map(JsonNode::asText)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }
}
