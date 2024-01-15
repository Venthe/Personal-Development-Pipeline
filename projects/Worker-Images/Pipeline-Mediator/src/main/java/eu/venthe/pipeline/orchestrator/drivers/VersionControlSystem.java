package eu.venthe.pipeline.orchestrator.drivers;

import java.util.Optional;

public interface VersionControlSystem {
    Optional<byte[]> getFile(String repositoryId, String projectName, String ref, String workflow);
}
