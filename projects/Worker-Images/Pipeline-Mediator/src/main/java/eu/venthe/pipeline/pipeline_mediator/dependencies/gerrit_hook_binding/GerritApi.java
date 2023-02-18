package eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpMethod.GET;

@Component
@RequiredArgsConstructor
public class GerritApi {
    private final GerritHeaders gerritHeaders;
    private final GerritUrls gerritUrls;
    private final RestTemplate restTemplate;
    private final GerritHookMapper hookMapper;

    public JsonNode getAllRevisions(String changeId, HttpEntity<?> traceId) {
        return map(call(gerritUrls.getAllRevisions(changeId), traceId));
    }

    public JsonNode getCommitForRevision(String changeId, String revisionId, HttpEntity<?> traceId) {
        return map(call(gerritUrls.commitForRevision(changeId, revisionId), traceId));
    }

    public JsonNode getCommitFilesForRevision(String changeId, String revisionId, HttpEntity<?> traceId) {
        return map(call(gerritUrls.filesForRevision(changeId, revisionId), traceId));
    }

    public JsonNode getCommitForProject(String projectId, String revision, HttpEntity<?> traceId) {
        return map(call(gerritUrls.getCommitForProject(projectId, revision), traceId));
    }

    public String getRevisionForBranchOrRevision(String projectId, String revision, HttpEntity<?> traceId) {
        return map(call(gerritUrls.getRevisionForBranchOrRevision(projectId, revision), traceId)).get("revision").asText();
    }

    public JsonNode getCommitFilesForProject(String projectName, String revision, HttpEntity<?> traceId) {
        return map(call(gerritUrls.getCommitFilesForProject(projectName, revision), traceId));
    }

    @NotNull
    private Supplier<ResponseEntity<String>> call(String url, HttpEntity<?> entity) {
        return () -> restTemplate.exchange(url, GET, entity, String.class);
    }

    @SneakyThrows
    private JsonNode map(Supplier<ResponseEntity<String>> supplier) {
        return hookMapper.mapToNodes(supplier.get().getBody());
    }

    public HttpEntity<?> generateTraceId() {
        return getHeaders(gerritHeaders.prepareHeaders(getTraceId()));
    }

    private static String getTraceId() {
        return randomUUID().toString();
    }

    @NotNull
    private static HttpEntity<?> getHeaders(@NotNull HttpHeaders httpHeaders1) {
        return new HttpEntity<>(httpHeaders1);
    }
}
