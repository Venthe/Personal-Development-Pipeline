package eu.venthe.combined.api;

import com.fasterxml.jackson.databind.JsonNode;
import eu.venthe.combined.configuration.MockServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockServerConfiguration.class})
@ActiveProfiles("test")
class SwaggerEndpointTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void resources() {
        final ResponseEntity<JsonNode> forObject = restTemplate.getForEntity("http://localhost:" + port + "/v3/api-docs", JsonNode.class);
        Assertions.assertThat(forObject.getStatusCode()).isEqualTo(HttpStatus.OK);

        log.info(Objects.requireNonNull(forObject.getBody()).toPrettyString());
    }

    @Test
    void ui() {
        final ResponseEntity<String> forObject = restTemplate.getForEntity("http://localhost:" + port + "/swagger-ui/", String.class);
        Assertions.assertThat(forObject.getStatusCode()).isEqualTo(HttpStatus.OK);

        log.info(Objects.requireNonNull(forObject.getBody()));
    }
}
