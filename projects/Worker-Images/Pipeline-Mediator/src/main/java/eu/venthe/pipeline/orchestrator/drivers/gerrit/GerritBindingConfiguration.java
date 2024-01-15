package eu.venthe.pipeline.orchestrator.drivers.gerrit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "gerrit")
public class GerritBindingConfiguration {
    String url;
    String username;
    String password;
//    Binding binding;
//    Set<String> ignoredEvents = new HashSet<>();
//    @Data
//    public static class Binding {
//        String topic;
//    }
}
