package eu.venthe.combined.api;

import com.fasterxml.jackson.databind.JsonNode;
import eu.venthe.combined.configuration.MockServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockServerConfiguration.class})
@ActiveProfiles("test")
class ActuatorEndpointTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest
    @MethodSource("jsonArguments")
    void verifyExistingJsonActuatorEndpoints(String actuatorPath) {
        verifyActuatorEndpoint(actuatorPath, JsonNode::toPrettyString, JsonNode.class);
    }

    @ParameterizedTest
    @MethodSource("plainTextArguments")
    void verifyExistingPlaintextActuatorEndpoints(String actuatorPath) {
        verifyActuatorEndpoint(actuatorPath, Object::toString, String.class);
    }

    @ParameterizedTest
    @MethodSource("memoryArguments")
    @Disabled("Mapping heapdump")
    void verifyExistingMemoryActuatorEndpoints(String actuatorPath) {
        verifyActuatorEndpoint(actuatorPath, a -> "", Object.class);
    }

    private <T> void verifyActuatorEndpoint(String actuatorPath1, Function<T, String> stringifier, Class<T> classType) {
        final ResponseEntity<T> result = restTemplate.getForEntity("http://localhost:" + port + actuatorPath1, classType);

        log.info(Objects.requireNonNull(stringifier.apply(result.getBody())));

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private static Stream<Arguments> jsonArguments() {
        return Stream.of(
                Arguments.of("/actuator"),
                Arguments.of("/actuator/beans"),
                Arguments.of("/actuator/caches"),
                // "/actuator/caches/{cache}"
                Arguments.of("/actuator/health"),
                // "/actuator/health/{path}"
                Arguments.of("/actuator/health/liveness"),
                Arguments.of("/actuator/health/livenessState"),
                Arguments.of("/actuator/health/readiness"),
                Arguments.of("/actuator/health/readinessState"),
                Arguments.of("/actuator/health/ping"),
                Arguments.of("/actuator/health/mongo"),
                Arguments.of("/actuator/health/kafka"),
                Arguments.of("/actuator/health/diskSpace"),
                Arguments.of("/actuator/info"),
                Arguments.of("/actuator/conditions"),
                Arguments.of("/actuator/configprops"),
                Arguments.of("/actuator/env"),
                // "/actuator/env/{toMatch}
                Arguments.of("/actuator/integrationgraph"),
                Arguments.of("/actuator/loggers"),
                // "/actuator/loggers/{name}
                Arguments.of("/actuator/threaddump"),
                Arguments.of("/actuator/metrics"),
                // "/actuator/metrics/{requiredMetricName}
                Arguments.of("/actuator/scheduledtasks"),
                Arguments.of("/actuator/mappings")
        );
    }

    private static Stream<Arguments> plainTextArguments() {
        return Stream.of(
                Arguments.of("/actuator/prometheus")
        );
    }

    private static Stream<Arguments> memoryArguments() {
        return Stream.of(
                Arguments.of("/actuator/heapdump")
        );
    }
}
