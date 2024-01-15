package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.CollectionUtilities;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class OnContextBranches {
    private final ObjectNode root;

    public static OnContextBranches create(ObjectNode root) {
        return new OnContextBranches(root);
    }

    public List<String> branches() {
        return params("branches");
    }

    public List<String> branchesIgnore() {
        return params("branchesIgnore");
    }

    @NotNull
    private List<String> params(String branches) {
        return Optional.ofNullable(root.get(branches))
                .map(JsonNode::elements)
                .map(elements -> CollectionUtilities.iteratorToStream(elements)
                        .map(JsonNode::asText)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }
}
