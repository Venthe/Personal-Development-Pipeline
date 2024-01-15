/*
package eu.venthe.pipeline.orchestrator.application.event_handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HandlerRegistry {
    private final List<EventHandler<?>> handlers;

    void handle(PipelineEvent event) {
        handlers.stream()
                .filter(handler -> handler.getDiscriminator().equals(event.getType()))
                .findFirst().orElseThrow()
                .handle(event);
    }
}
*/
