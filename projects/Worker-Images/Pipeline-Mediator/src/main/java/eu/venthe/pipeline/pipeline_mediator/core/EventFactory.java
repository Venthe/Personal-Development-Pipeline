package eu.venthe.pipeline.pipeline_mediator.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.pipeline_mediator.api.model.EventTriggerDto;
import eu.venthe.pipeline.pipeline_mediator.core.model.AbstractContinuousIntegrationEvent;
import eu.venthe.pipeline.pipeline_mediator.core.model.ChangeEvent;
import eu.venthe.pipeline.pipeline_mediator.core.model.DispatchEvent;
import eu.venthe.pipeline.pipeline_mediator.core.model.RevisionEvent;
import eu.venthe.pipeline.pipeline_mediator.core.model.vo.EventType;
import eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding.GerritApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

import static eu.venthe.pipeline.pipeline_mediator.core.model.AbstractContinuousIntegrationEvent.typeProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventFactory {
    private final ObjectMapper objectMapper;
    private final GerritApi gerritApi;

    public AbstractContinuousIntegrationEvent fromTrigger(EventTriggerDto trigger) {
        if (!EventType.DISPATCH_EVENTS.contains(trigger.getType())) {
            throw new UnsupportedOperationException("Cannot trigger this event %s".formatted(trigger));
        }

        return DispatchEvent.create(trigger, objectMapper, gerritApi);

    }

    public Optional<AbstractContinuousIntegrationEvent> fromWebhook(ObjectNode event) {
        log.debug("Queued event {}", event);
        String type = typeProvider(event);

        if (EventType.CHANGE_EVENTS.contains(type)) {
            return Optional.of(ChangeEvent.create(event, objectMapper, gerritApi));
        } else if (EventType.REVISION_EVENTS.contains(type)) {
            return Optional.of(RevisionEvent.create(event, objectMapper, gerritApi));
        } else if (EventType.IGNORED_EVENTS.contains(type)) {
            log.debug("Event skipped {}", event);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("This event is not handled by the system: {0}", type));
        }
    }
}
