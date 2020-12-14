package eu.venthe.combined.kafka.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, String> kafka;

    /**
     * Return an indication of health.
     *
     * @return the health for
     */
    @Override
    public Health health() {
        try {
            kafka.send("kafka-health-indicator", "❥").get(100, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | TimeoutException e) {
            return Health.down(e).build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Health.up().build();
    }
}
