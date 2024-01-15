package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.utilities.ContextUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InputsContext {
    private static final String INPUTS = "inputs";
    private final ObjectNode root;

    public static Optional<InputsContext> create(ObjectNode root) {
        return ContextUtilities.get(root, INPUTS, InputsContext::new);
    }

    public Map<String, ObjectNode> getContext() {
        return Map.of(INPUTS, root);
    }

    public JsonNode getInput(String key) {
        return root.get(key);
    }

    public List<String> getInputKeys() {
        return root.properties().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
