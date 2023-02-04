package eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding.Utilities.getByPath;

@Slf4j
@Repository
public class MessageRepository {
    private static final Map<String, ObjectNode> MESSAGES = new HashMap<>();

    @Synchronized
    public boolean register(ObjectNode message) {
        String key = generateKey(message);
        Optional<ObjectNode> message1 = Optional.ofNullable(MESSAGES.get(key));
        if (message1.isPresent()) {
            return false;
        }

        MESSAGES.put(key, message);
        return true;
    }

    private String generateKey(ObjectNode message) {
        String type = getByPath(message, "type", JsonNode::asText).orElseThrow();
        switch (type) {
            case "change-merged":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "refName",
                        "newRev"
                        );
            case "wip-state-changed":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "patchSet.ref",
                        "change.project",
                        "change.id"
                        );
            case "private-state-changed":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "project.name",
                        "additionalProperties.change.revision.ref",
                        "change.id"
                        );
            case "patchset-created":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "change.project",
                        "additionalProperties.change.revision.ref",
                        "change.id"
                        );
            case "ref-updated":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "refUpdate.refName",
                        "refUpdate.project",
                        "refUpdate.oldRev",
                        "refUpdate.newRev"
                );
            case "comment-added":
                return digest(message,
                        "eventCreatedOn",
                        "type",
                        "change.project",
                        "additionalProperties.change.revision.ref",
                        "change.id"
                );
            default:
                log.warn("No idempotency key generator for type {}. {}", type, message.toPrettyString());
                return UUID.randomUUID().toString();
        }
    }

    private static String digest(ObjectNode message, String... paths) {
        String key = Arrays.stream(paths)
                .sorted(String::compareTo)
                .map(path -> getByPath(message, path, JsonNode::asText).orElseThrow(() -> new RuntimeException(MessageFormat.format("{0} {1}", path, message.toPrettyString()))))
                .collect(Collectors.joining("_"));
        return new String(DigestUtils.sha512(key), StandardCharsets.UTF_8);
    }
}
