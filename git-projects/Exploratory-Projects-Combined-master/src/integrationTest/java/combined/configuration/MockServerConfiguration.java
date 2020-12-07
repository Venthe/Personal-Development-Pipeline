package eu.venthe.combined.configuration;

import lombok.Setter;
import org.mockserver.client.MockServerClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@ConfigurationProperties("mockserver")
@Setter
public class MockServerConfiguration {
    private String host;
    private Integer port;

    @Bean(destroyMethod = "")
    public MockServerClient mockServerClient(MockServerConfiguration mockServerConfiguration) {
        return new MockServerClient(mockServerConfiguration.host, mockServerConfiguration.port);
    }
}
