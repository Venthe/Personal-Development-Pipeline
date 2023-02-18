package eu.venthe.pipeline.pipeline_mediator.dependencies.jenkins;

import eu.venthe.pipeline.gerrit_mediator.gerrit.api.DefaultApi;
import eu.venthe.pipeline.gerrit_mediator.jenkins.invoker.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsConfiguration {
    String user;
    String token;
    String uri;
    String jobName;

    @Bean
    public DefaultApi api(JenkinsConfiguration configuration, RestTemplate template) {
        ApiClient api = new ApiClient(template);
        api.setUsername(configuration.getUser());
        api.setPassword(configuration.getToken());
        api.setBasePath(configuration.getUri());

        return new DefaultApi(api);
    }
}
