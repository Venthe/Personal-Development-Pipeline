/*
package eu.venthe.pipeline.orchestrator.archive.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.archive.core.MediatorService;
import eu.venthe.pipeline.orchestrator.archive.configuration.YamlMapper;
import eu.venthe.pipeline.orchestrator.archive.dependencies.transport.QueuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/gerrit")
public class GerritWebhook {
    private final MediatorService mediatorService;
    private final YamlMapper yamlMapper;

    @PostMapping(
            value = "/handle-event",
            consumes = {MediaType.APPLICATION_JSON_VALUE, CustomMediaTypes.APPLICATION_X_YAML}
    )
    public ResponseEntity<QueuedEvent> gerritEvent(@RequestBody String event) {
        try {
            return ResponseEntity.of(mediatorService.handleGerritEvent((ObjectNode) yamlMapper.getObjectMapper().readTree(event)));
        } catch (Exception exception) {
            log.error("General error in handling of the event: {}", event, exception);
            // Response 200 to mark as delivered in gerrit
            return ResponseEntity.ok().build();
        }
    }
}
*/
