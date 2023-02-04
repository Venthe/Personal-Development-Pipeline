package eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding;

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
        String replacedBody = removeGerritMagicPrefix(body);
        return objectMapper.readTree(replacedBody);
    }

    @NotNull
    private static String removeGerritMagicPrefix(String body) {
        return body.replaceAll("\\)]}'\\n", "");
    }
}
