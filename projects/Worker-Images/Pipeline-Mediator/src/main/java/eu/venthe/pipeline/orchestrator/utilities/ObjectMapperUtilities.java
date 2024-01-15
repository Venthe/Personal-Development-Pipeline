package eu.venthe.pipeline.orchestrator.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@UtilityClass
public class ObjectMapperUtilities {
    public static Map<String, String> transformToKeyValuePairs(JsonNode node) {
        return transformToKeyValuePairs(node, new HashMap<>(), "");
    }

    private static Map<String, String> transformToKeyValuePairs(JsonNode node, Map<String, String> result, String prefix) {
        if (node.isObject()) {
            ObjectNode on = (ObjectNode) node;
            on.fields().forEachRemaining(f -> {
                String prefix1 = !prefix.isBlank() ? prefix + "." + f.getKey() : f.getKey();
                transformToKeyValuePairs(f.getValue(), result, prefix1);
            });
        } else if (node.isArray()) {
            ArrayNode an = (ArrayNode) node;
            Iterator<JsonNode> it = an.elements();
            for (int i = 0; it.hasNext(); ) {
                JsonNode val = it.next();
                transformToKeyValuePairs(val, result, prefix + "[" + i + "]");
            }
        } else {
            result.put(prefix, node.asText());
        }

        return result;
    }
}
