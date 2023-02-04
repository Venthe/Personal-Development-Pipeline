package eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.pipeline_mediator.jenkins.JenkinsApi;
import eu.venthe.pipeline.pipeline_mediator.transport.KafkaApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.function.Supplier;

import static eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding.Utilities.getByPath;
import static eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding.Utilities.noop;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpMethod.GET;

@Controller
@RequestMapping("${openapi.gerrit.base-path:}")
@Slf4j
@RequiredArgsConstructor
public class GerritHookController {
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final KafkaApi kafkaApi;
    private final GerritHeaders gerritHeaders;
    private final GerritUrl gerritUrl;
    private final GerritHookMapper mapper;
    private final GerritBindingConfiguration configuration;
    private final JenkinsApi jenkinsApi;

    @PostMapping(
            value = "/handle-event",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Void> patchsetCreatedPost(@RequestBody JsonNode event,
                                                    @RequestHeader HttpHeaders headers) {
        ObjectNode result = event.deepCopy();

        String type = getByPath(result, "type", JsonNode::asText).orElseThrow();
        String typeMarker = "[" + type + "]";

        if (configuration.getIgnoredEvents().contains(type)) {
            log.debug(type + "Event ignored, {}", result.toPrettyString());
            return ResponseEntity.accepted().build();
        }

        Optional<String> _changeId = getByPath(result, "change.id", JsonNode::asText);
        _changeId.ifPresentOrElse(noop(), () -> log.warn(typeMarker + " change.id not present"));

        Optional<String> _revisionId = getByPath(result, "patchSet.revision", JsonNode::asText);
        _revisionId.ifPresentOrElse(noop(), () -> log.warn(typeMarker + " patchSet.revision not present"));

        HttpEntity<?> requestHeaders = getEntity(gerritHeaders.prepareHeaders(getTraceId()));

        _changeId.ifPresent(changeId -> {
            JsonNode allRevisions = map(call(gerritUrl.getAllRevisions(changeId), requestHeaders));

            _revisionId.ifPresent(revisionId -> {
                ObjectNode additionalProperties = objectMapper.createObjectNode();

                additionalProperties.set("commit", getCommit(requestHeaders, changeId, revisionId));
                additionalProperties.set("files", getFiles(requestHeaders, changeId, revisionId));
                additionalProperties.set("change", getChange(allRevisions, revisionId));

                result.set("additionalProperties", additionalProperties);
            });
        });

        boolean register = messageRepository.register(result);
        if (!register) {
            log.debug(type + "Event already handled, {}", result.toPrettyString());
            return ResponseEntity.accepted().build();
        }

        //log.info(typeMarker + " Event {}", result.toPrettyString());

        //String kafkaMessageKey = _changeId.orElse(null);
        // kafkaApi.send(kafkaMessageKey, result.toPrettyString());
        jenkinsApi.jobJobNameBuildWithParametersPostWithHttpInfo(result.toString());

        return ResponseEntity.accepted().build();
    }

    private JsonNode getCommit(HttpEntity<?> requestHeaders, String changeId, String revisionId) {
        return map(call(gerritUrl.commitForRevision(changeId, revisionId), requestHeaders));
    }

    private JsonNode getFiles(HttpEntity<?> requestHeaders, String changeId, String revisionId) {
        return map(call(gerritUrl.filesForRevision(changeId, revisionId), requestHeaders));
    }

    @NotNull
    private static ObjectNode getChange(JsonNode allRevisions, String revisionId) {
        JsonNode thisRevision = allRevisions.get("revisions").get(revisionId);
        ObjectNode change = allRevisions.deepCopy();
        change.remove("revisions");
        change.set("revision", thisRevision);
        return change;
    }

    @NotNull
    private Supplier<ResponseEntity<String>> call(String url, HttpEntity<?> entity) {
        return () -> restTemplate.exchange(url, GET, entity, String.class);
    }

    @NotNull
    private static HttpEntity<?> getEntity(@NotNull HttpHeaders httpHeaders1) {
        return new HttpEntity<>(httpHeaders1);
    }

    @SneakyThrows
    private JsonNode map(Supplier<ResponseEntity<String>> supplier) {
        String body = supplier.get().getBody();
        validateBodyNotNull(body);
        return mapper.mapToNodes(body);
    }

    private static void validateBodyNotNull(String body) {
        if (body == null) {
            throw new RuntimeException();
        }
    }

    private static String getTraceId() {
        return randomUUID().toString();
    }
}
