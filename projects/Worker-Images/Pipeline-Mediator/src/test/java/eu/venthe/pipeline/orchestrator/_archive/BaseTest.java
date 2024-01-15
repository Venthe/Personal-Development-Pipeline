/*
package eu.venthe.pipeline.gerrit_mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.clientt.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Function;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "logging.level.reactor.netty.channel=debug"
})
@ExtendWith(SpringExtension.class)
public class BaseTest {
    @LocalServerPort
    Integer port;

    WebClient application;

    @BeforeEach
    void webClient() {
        application = WebClient.create("http://localhost:" + port);
    }

    static <T> Function<ClientResponse, Mono<Tuple2<ClientResponse, T>>> responseWithBody(Class<T> elementClass) {
        return a -> Mono.zip(Mono.just(a), a.bodyToMono(elementClass));
    }
}
*/
