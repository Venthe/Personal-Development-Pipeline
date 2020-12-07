package eu.venthe.combined.api;

import eu.venthe.combined.configuration.MockServerConfiguration;
import eu.venthe.combined.microservice.MicroserviceBodyDto;
import eu.venthe.combined.microservice.MicroserviceService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockServerConfiguration.class})
@ActiveProfiles("test")
class MicroserviceControllerTest {


    @Autowired
    private MicroserviceService microserviceService;
    @Autowired
    private MockServerClient mockServerClient;

    @BeforeEach
    void clearAll() {
        mockServerClient.reset();
    }

    @ParameterizedTest
    @MethodSource("test")
    void test(String path, HttpRequest get, HttpResponse response, MicroserviceBodyDto expectedMicroserviceBodyDto) {
        // Given
        mockServerClient.when(get).respond(response);

        // When
        final MicroserviceBodyDto test = microserviceService.getInformation(path);

        // Then
        Assertions.assertThat(test).usingRecursiveComparison().isEqualTo(expectedMicroserviceBodyDto);
    }

    private static Stream<Arguments> test() {
        return Stream.of(
                Arguments.of(
                        "Test",
                        request()
                                .withMethod("GET")
                                .withPath("/hello-world/test"),
                        response()
                                .withStatusCode(200)
                                .withBody("{\"name\":\"Test\",\"surname\":\"CONST\"}", MediaType.APPLICATION_JSON),
                        new MicroserviceBodyDto("Test", "CONST")
                ),
                Arguments.of(
                        "Test2",
                        request()
                                .withMethod("GET")
                                .withPath("/hello-world/test2"),
                        response()
                                .withStatusCode(200)
                                .withBody("{\"name\":\"Test2\",\"surname\":\"CONST\"}", MediaType.APPLICATION_JSON),
                        new MicroserviceBodyDto("Test2", "CONST")
                )
        );
    }
}
