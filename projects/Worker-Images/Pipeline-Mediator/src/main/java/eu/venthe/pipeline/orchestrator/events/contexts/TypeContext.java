package eu.venthe.pipeline.orchestrator.events.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.venthe.pipeline.orchestrator.events.EventType;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class TypeContext {
    public static Optional<EventType> type(ObjectNode root) {
        return getType(root, "type");
    }

    @NotNull
    private static Optional<EventType> getType(ObjectNode root, String type) {
        return Optional.ofNullable((TextNode) root.get(type))
                .filter(Predicate.not(JsonNode::isNull))
                .map(TextNode::asText)
                .flatMap(EventType::of);
    }
}
