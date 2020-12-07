package eu.venthe.combined.microservice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("microservice")
@Data
public class MicroserviceConfiguration {
    private String uri;
}
