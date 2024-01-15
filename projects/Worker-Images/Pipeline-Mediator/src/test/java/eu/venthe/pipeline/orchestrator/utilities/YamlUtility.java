package eu.venthe.pipeline.orchestrator.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.venthe.pipeline.orchestrator.utilities.GraphUtility;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class YamlUtility {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    @SneakyThrows
    public static JsonNode parseYaml(String value) {
        return OBJECT_MAPPER.readTree(value);
    }

    public static JsonNodeFactory getNodeFactory() {
        return OBJECT_MAPPER.getNodeFactory();
    }
}
