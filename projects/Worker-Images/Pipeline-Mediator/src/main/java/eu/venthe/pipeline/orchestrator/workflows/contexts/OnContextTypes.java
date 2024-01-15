package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static eu.venthe.pipeline.orchestrator.utilities.CollectionUtilities.iteratorToStream;

@RequiredArgsConstructor
public class OnContextTypes {
    private final ObjectNode root;

    public static Optional<OnContextTypes> create(ObjectNode root) {
        return ContextUtilities.get(root, "types", OnContextTypes::new);
    }

    public Optional<String> text() {
        return Optional.ofNullable(root)
                .filter(JsonNode::isTextual)
                .map(ContainerNode::asText);
    }

    public Optional<List<String>> array() {
        return Optional.of(iteratorToStream(root.elements())
                .map(JsonNode::asText).toList());
    }
}
