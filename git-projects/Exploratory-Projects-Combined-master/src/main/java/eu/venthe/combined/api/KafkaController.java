package eu.venthe.combined.api;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kafka-manager")
public class KafkaController {
    private final KafkaTemplate<String, String> template;
    private final KafkaListenerEndpointRegistry registry;

    @PostMapping("/send/{data}")
    public void send(@PathVariable String data) {
        template.send("myTopic", data);
    }

    @PostMapping("/pause")
    public void pause() {
        registry.getListenerContainers().forEach(MessageListenerContainer::pause);
    }

    @PostMapping("/resume")
    public void resume() {
        registry.getListenerContainers().forEach(MessageListenerContainer::resume);
    }
}
