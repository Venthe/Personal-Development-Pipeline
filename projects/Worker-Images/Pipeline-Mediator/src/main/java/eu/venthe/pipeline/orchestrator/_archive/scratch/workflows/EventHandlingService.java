/*
package eu.venthe.pipeline.orchestrator.scratch.workflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.scratch.events.EventType;
import eu.venthe.pipeline.orchestrator.scratch.workflows.handlers.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventHandlingService {
    private final List<EventHandler> handlers;
    private final ObjectMapper objectMapper;

    public void handle(ObjectNode event) {
        log.debug("Event received for handling");

        handlers.stream()
                .filter(handler -> handler.getDiscriminator().equals(EventType.of(event.get("type").asText())))
                .findFirst().orElseThrow()
                .handle(event);

    }
}
*/
