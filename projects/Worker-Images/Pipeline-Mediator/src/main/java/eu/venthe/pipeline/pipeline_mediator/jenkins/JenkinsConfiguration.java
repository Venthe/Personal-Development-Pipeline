package eu.venthe.pipeline.pipeline_mediator.jenkins;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsConfiguration {
    String user;
    String token;
    String uri;
    String jobName;
}
