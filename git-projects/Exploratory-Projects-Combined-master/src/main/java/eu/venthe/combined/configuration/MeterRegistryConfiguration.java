package eu.venthe.combined.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.application")
@Data
public class MeterRegistryConfiguration {
    private String name;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(MeterRegistryConfiguration meterRegistryConfiguration) {
        return registry -> registry.config().commonTags("application", meterRegistryConfiguration.getName());
    }
}
