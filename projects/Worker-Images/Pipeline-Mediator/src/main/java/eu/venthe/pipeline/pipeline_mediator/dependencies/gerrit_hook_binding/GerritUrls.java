package eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GerritUrls {
    private final GerritBindingConfiguration configuration;

    @NotNull
    public String getAllRevisions(String changeId) {
        return getUrl("changes/%s?o=ALL_REVISIONS".formatted(changeId));
    }

    @NotNull
    public String filesForRevision(String changeId, String revisionId) {
        return getUrl("changes/%s/revisions/%s/files".formatted(changeId, revisionId));
    }

    @NotNull
    public String commitForRevision(String changeId, String revisionId) {
        return getUrl("changes/%s/revisions/%s/commit".formatted(changeId, revisionId));
    }

    @NotNull
    public String getCommitFilesForProject(String project, String revision) {
        return getUrl("projects/%s/commits/%s/files".formatted(project, revision));
    }

    @NotNull
    public String getCommitForProject(String project, String revisionId) {
        return getUrl("projects/%s/commits/%s".formatted(project, revisionId));
    }

    @NotNull
    public String getRevisionForBranchOrRevision(String projectId, String revision) {
        return getUrl("projects/%s/branches/%s".formatted(projectId, revision));
    }

    @NotNull
    private String getUrl(String url) {
        return "%s/a/%s".formatted(configuration.getUrl(), url);
    }
}
