package eu.venthe.pipeline.orchestrator.drivers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.*;

@Component
@ConditionalOnProperty(value = "versionControlSystem", havingValue = "test", matchIfMissing = false)
public class TestVersionControlSystem implements VersionControlSystem {

    private final Map<FileRef, String> files = new HashMap<>();

    public String addFile(FileRef fileRef, String content) {
        return files.put(fileRef, content);
    }

    @Override
    public Optional<byte[]> getFile(String repositoryId, String projectName, String ref, String path) {
        return Optional.ofNullable(files.get(new FileRef(repositoryId, projectName, ref, path)))
                .map(String::getBytes);
    }

    public List<String> getFiles(String project, String ref, String glob) {
        return files.entrySet().stream()
                .filter(e -> e.getKey().projectName().equalsIgnoreCase(project))
                .filter(e -> e.getKey().ref().equalsIgnoreCase(ref))
                .filter(e -> new AntPathMatcher("/").match(glob, e.getKey().path()))
                .map(Map.Entry::getValue)
                .toList();
    }


    public record FileRef(String repositoryId, String projectName, String ref, String path) {
        public FileRef {
            repositoryId = repositoryId.toLowerCase(Locale.ROOT);
            projectName = projectName.toLowerCase(Locale.ROOT);
            ref = ref.toLowerCase(Locale.ROOT);
            path = path.toLowerCase(Locale.ROOT);
        }
    }

}
