package eu.venthe.pipeline.orchestrator.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static eu.venthe.pipeline.orchestrator.utilities.ObjectMapperUtilities.transformToKeyValuePairs;

@Component
@RequiredArgsConstructor
public class ExpressionUtilities {
    private final ObjectMapper objectMapper;

    public String evaluate(String expression, ObjectNode context) {
        return evaluate(expression, transformToKeyValuePairs(context));
    }

    public <T> String evaluate(String expression, Map<String, T> properties) {
        return new StringSubstitutor(properties, "${{", "}}").replace(expression);
    }

    public String evaluateOn(String expression, Map<String, JsonNode> properties) {
        return evaluate(expression, properties.entrySet().stream().map(e->Map.entry(e.getKey(), toContext(properties))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public ObjectNode toContext(Map<String, JsonNode> nodes) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        nodes.forEach(objectNode::set);
        return objectNode;
    }
}
