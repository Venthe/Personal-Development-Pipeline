package eu.venthe.pipeline.pipeline_mediator.configuration;

import eu.venthe.pipeline.pipeline_mediator.api.model.EventTriggerDto;
import eu.venthe.pipeline.pipeline_mediator.dependencies.transport.QueuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@TestComponent
@RequiredArgsConstructor
public class TestEventApi {
    public Integer port;

    private final RestTemplate template;

    public ResponseEntity<QueuedEvent> queueEvent(EventTriggerDto eventTriggerDto) {
        return template.postForEntity(("localhost:%s/event/queue".formatted(port)), eventTriggerDto, QueuedEvent.class);
    }
}
