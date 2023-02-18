package eu.venthe.pipeline.pipeline_mediator.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.pipeline.pipeline_mediator.configuration.YamlMapper;
import eu.venthe.pipeline.pipeline_mediator.core.MediatorService;
import eu.venthe.pipeline.pipeline_mediator.dependencies.transport.QueuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class QueuedEventListener {
    private final ObjectMapper objectMapper;
    private final MediatorService mediatorService;
    private final YamlMapper yamlMapper;

    @KafkaListener(topics = "#{@gerritBindingConfiguration.binding.topic}")
    @RetryableTopic(
            attempts = "8",
            backoff = @Backoff(delay = 10000, multiplier = 2.2),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void listenToQueuedEvents(String event) {
        try {
            mediatorService.receiveQueuedEvent(yamlMapper.getObjectMapper().readValue(event, QueuedEvent.class));
        } catch (Exception e) {
            log.warn("There has been a probblem queueing the event: {}", event, e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    @DltHandler
    public void handleDeadLetterQueue(String event) {
        log.warn("Received DLT Message " + event);
        try {
            mediatorService.registerFailedQueuedEvent(yamlMapper.getObjectMapper().readValue(event, QueuedEvent.class));
        } catch (Exception exception) {
            log.error("Unrecoverable error for event: {}", event, exception);
        }
    }
}
