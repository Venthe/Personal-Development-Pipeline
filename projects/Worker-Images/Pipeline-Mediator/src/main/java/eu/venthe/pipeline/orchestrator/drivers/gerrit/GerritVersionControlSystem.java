package eu.venthe.pipeline.orchestrator.drivers.gerrit;

import eu.venthe.pipeline.orchestrator.drivers.VersionControlSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(value = "versionControlSystem", havingValue = "gerrit", matchIfMissing = false)
public class GerritVersionControlSystem implements VersionControlSystem {
    private final GerritApi gerritApi;

    @Override
    public Optional<byte[]> getFile(String repositoryId, String projectName, String ref, String path) {
        return gerritApi.getFileForBranch(projectName, ref, path, gerritApi.generateTraceId());
    }
}
