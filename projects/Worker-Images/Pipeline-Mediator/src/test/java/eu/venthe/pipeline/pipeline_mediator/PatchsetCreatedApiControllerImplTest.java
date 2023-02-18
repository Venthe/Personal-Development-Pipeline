/*
package eu.venthe.pipeline.pipeline_mediator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.pipeline.pipeline_mediator.api.GerritHookController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@Disabled
class PatchsetCreatedApiControllerImplTest {
    @Autowired
    GerritHookController patchsetCreatedApiController;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void name() throws JsonProcessingException {
        JsonNode event = readEvent("events/patchset-created.json");

        log.info("{}", patchsetCreatedApiController.patchsetCreatedPost(event));
    }

    private JsonNode readEvent(String name) throws JsonProcessingException {
        return objectMapper.readTree(getFile(name));
    }

    private String getFile(String name) {
        URL resource = getClass()
                .getClassLoader()
                .getResource(name);
        if (resource == null) {
            throw new RuntimeException();
        }

        File file = new File(resource.getFile());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
*/