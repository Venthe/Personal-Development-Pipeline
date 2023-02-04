package eu.venthe.pipeline.pipeline_mediator.transport;

import eu.venthe.pipeline.pipeline_mediator.gerrit_hook_binding.GerritBindingConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaApi {
    private final GerritBindingConfiguration configuration;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @SneakyThrows
    public SendResult<String, Object> send(String key, Object object) {
        return kafkaTemplate.send(topic(), key, object).get();
    }

    private String topic() {
        return configuration.getBinding().getTopic();
    }
}
