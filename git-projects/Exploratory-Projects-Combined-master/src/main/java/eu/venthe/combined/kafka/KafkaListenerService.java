package eu.venthe.combined.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaListenerService {
    public void act(ConsumerRecord<?, ?> cr) {
        log.info(cr.toString());
    }
}
