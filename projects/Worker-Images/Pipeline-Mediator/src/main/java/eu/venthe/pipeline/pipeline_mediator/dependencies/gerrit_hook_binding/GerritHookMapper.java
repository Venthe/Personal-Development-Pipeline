package eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GerritHookMapper {
    private final ObjectMapper objectMapper;

    public JsonNode mapToNodes(String body) throws JsonProcessingException {
        validateBodyNotNull(body);
        String replacedBody = removeGerritMagicPrefix(body);
        return objectMapper.readTree(replacedBody);
    }

    private static void validateBodyNotNull(String body) {
        if (body == null) {
            throw new RuntimeException("Body cannot be null");
        }
    }

    @NotNull
    private static String removeGerritMagicPrefix(String body) {
        return body.replaceAll("\\)]}'\\n", "");
    }
}
