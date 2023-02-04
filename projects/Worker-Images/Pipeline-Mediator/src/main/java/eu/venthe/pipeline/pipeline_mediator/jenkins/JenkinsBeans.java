package eu.venthe.pipeline.pipeline_mediator.jenkins;

import eu.venthe.pipeline.gerrit_mediator.gerrit.api.DefaultApi;
import eu.venthe.pipeline.gerrit_mediator.jenkins.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JenkinsBeans {
    @Bean
    public DefaultApi api(JenkinsConfiguration configuration, RestTemplate template) {
        ApiClient api = new ApiClient(template);
        api.setUsername(configuration.getUser());
        api.setPassword(configuration.getToken());
        api.setBasePath(configuration.getUri());

        return new DefaultApi(api);
    }
}
