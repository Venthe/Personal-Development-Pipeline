package eu.venthe.pipeline.orchestrator.utilities;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

@UtilityClass
public class GlobPatternMatching {
    public static boolean isMatching(String pattern, String path) {
//        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

//        return matcher.matches(Paths.get(path));
        return FilenameUtils.wildcardMatch(path, pattern);
    }

}
