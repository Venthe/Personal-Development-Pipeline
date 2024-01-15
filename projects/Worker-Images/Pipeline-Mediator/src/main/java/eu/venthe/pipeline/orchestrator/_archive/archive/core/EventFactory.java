/*
package eu.venthe.pipeline.orchestrator.archive.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.archive.api.model.EventTriggerDto;
import eu.venthe.pipeline.orchestrator.archive.core.model.AbstractContinuousIntegrationEvent;
import eu.venthe.pipeline.orchestrator.archive.core.model.ChangeEvent;
import eu.venthe.pipeline.orchestrator.archive.core.model.DispatchEvent;
import eu.venthe.pipeline.orchestrator.archive.core.model.RevisionEvent;
import eu.venthe.pipeline.orchestrator.archive.core.model.vo.OldEventType;
import eu.venthe.pipeline.orchestrator.archive.dependencies.gerrit_hook_binding.GerritApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventFactory {
    private final ObjectMapper objectMapper;
    private final GerritApi gerritApi;

    public AbstractContinuousIntegrationEvent fromTrigger(EventTriggerDto trigger) {
        if (!OldEventType.DISPATCH_EVENTS.contains(trigger.getType())) {
            throw new UnsupportedOperationException("Cannot trigger this event %s".formatted(trigger));
        }

        return DispatchEvent.create(trigger, objectMapper, gerritApi);

    }

    public Optional<AbstractContinuousIntegrationEvent> fromWebhook(ObjectNode event) {
        log.debug("Queued event {}", event);
        String type = AbstractContinuousIntegrationEvent.typeProvider(event);

        if (OldEventType.CHANGE_EVENTS.contains(type)) {
            return Optional.of(ChangeEvent.create(event, objectMapper, gerritApi));
        } else if (OldEventType.REVISION_EVENTS.contains(type)) {
            return Optional.of(RevisionEvent.create(event, objectMapper, gerritApi));
        } else if (OldEventType.IGNORED_EVENTS.contains(type)) {
            log.debug("Event skipped {}", event);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("This event is not handled by the system: {0}", type));
        }
    }
}
*/
