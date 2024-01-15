package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.CollectionUtilities;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OnContextInputs {
    private final ObjectNode root;

    public static Optional<OnContextInputs> create(ObjectNode root) {
        return ContextUtilities.get(root, "inputs", OnContextInputs::new);
    }

    public List<Input> requiredInputs() {
        if(!root.isObject()) throw new UnsupportedOperationException();

        return root.properties().stream()
                .filter(e ->e.getValue().isObject())
                .map(e-> new Input(
                        e.getKey(),
                        Optional.ofNullable(e.getValue().get("required"))
                                .map(JsonNode::asBoolean)
                                .orElse(false)
                ))
                .filter(Input::getRequired)
                .toList();
    }

    @RequiredArgsConstructor
    @Getter
    public class Input {
        private final String key;
        private final Boolean required;

    }
}
