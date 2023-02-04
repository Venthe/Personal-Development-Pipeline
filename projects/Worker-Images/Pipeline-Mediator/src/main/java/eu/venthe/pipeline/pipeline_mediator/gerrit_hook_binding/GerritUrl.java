package eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GerritUrl {
    private final GerritBindingConfiguration configuration;

    @NotNull
    public String getAllRevisions(String changeId) {
        return getUrl("changes/" + changeId + "?o=ALL_REVISIONS");
    }

    @NotNull
    public String filesForRevision(String changeId, String revisionId) {
        return getUrl("changes/" + changeId + "/revisions/" + revisionId + "/files");
    }

    @NotNull
    public String commitForRevision(String changeId, String revisionId) {
        return getUrl("changes/" + changeId + "/revisions/" + revisionId + "/commit");
    }

    @NotNull
    private String getUrl(String url) {
        return configuration.getUrl() + "/a/" + url;
    }
}
