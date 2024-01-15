package eu.venthe.pipeline.orchestrator.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class ContextUtilities {
    public static <T> Optional<T> get(JsonNode root, String path, Function<ObjectNode, T> creator) {
        return Optional.ofNullable(root.get(path))
                .filter(Predicate.not(JsonNode::isNull))
                .map(node -> creator.apply((ObjectNode) node));
    }
}
