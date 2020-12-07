package eu.venthe.combined.configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationHealthMicrometer {

    public ApplicationHealthMicrometer(MeterRegistry registry, HealthEndpoint healthEndpoint) {
        Gauge.builder("health", healthEndpoint, this::getStatusCode)
                .strongReference(true)
                .register(registry);
    }

    private int getStatusCode(HealthEndpoint health) {
        Status status = health.health().getStatus();
        if (status.equals(Status.UP)) return 3;
        if (status.equals(Status.OUT_OF_SERVICE)) return 2;
        if (status.equals(Status.DOWN)) return 1;
        if (status.equals(Status.UNKNOWN)) return 0;
        return -1;
    }

}
