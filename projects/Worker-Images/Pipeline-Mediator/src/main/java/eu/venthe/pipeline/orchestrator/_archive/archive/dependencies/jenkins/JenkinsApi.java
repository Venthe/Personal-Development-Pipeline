/*
package eu.venthe.pipeline.orchestrator.archive.dependencies.jenkins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.gerrit_mediator.gerrit.api.DefaultApi;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class JenkinsApi {
    private final DefaultApi defaultApi;
    private final JenkinsConfiguration jenkinsConfiguration;
    private final ObjectMapper objectMapper;

    public ResponseEntity<Void> triggerPipeline(ObjectNode event) throws JsonProcessingException {
        return prepareFileToSend(objectMapper.writeValueAsString(event), eventFile -> defaultApi.jobJobNameBuildWithParametersPostWithHttpInfo(jenkinsConfiguration.getJobName(), eventFile));
    }

    private ResponseEntity<Void> prepareFileToSend(String event, Function<File, ResponseEntity<Void>> action) {
        try {
            File tempFile = createTempFile();
            try (FileOutputStream eventFileStream = new FileOutputStream(tempFile)) {
                eventFileStream.write(event.getBytes(UTF_8));
                return action.apply(tempFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                tempFile.deleteOnExit();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static File createTempFile() throws IOException {
        return File.createTempFile(Long.toString(new Date().getTime()), "");
    }
}
*/
