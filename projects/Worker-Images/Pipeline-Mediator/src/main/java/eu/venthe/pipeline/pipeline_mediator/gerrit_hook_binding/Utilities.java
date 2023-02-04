package eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

@UtilityClass
public class Utilities {

    @NotNull
    public static <T> Consumer<T> noop() {
        return a -> {
        };
    }

    public static <T> Optional<T> getByPath(ObjectNode result, String path, Function<JsonNode, T> tObjectFunction) {
        String[] split = path.split("\\.");
        Optional<JsonNode> resultingNode = ofNullable(result);

        for (String forEach : split) {
            resultingNode = resultingNode.map(a->a.get(forEach));
        }

        return resultingNode
                .filter(Predicate.not(JsonNode::isMissingNode))
                .map(tObjectFunction);
    }
}
