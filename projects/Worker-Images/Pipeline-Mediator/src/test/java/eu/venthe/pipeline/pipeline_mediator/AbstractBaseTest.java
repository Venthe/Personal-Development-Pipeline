package eu.venthe.pipeline.pipeline_mediator;

import eu.venthe.pipeline.pipeline_mediator.configuration.TestEventApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.text.MessageFormat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestEventApi.class)
@ActiveProfiles("test")
public abstract class AbstractBaseTest {
    @LocalServerPort
    protected Integer port;

    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.8"));

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.18"));

    @Container
    private static final MockServerContainer mockServer = new MockServerContainer(DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion()));

    protected MockServerClient mockServerClient;

    @BeforeEach
    public void setupMockServerClient() {
        if (mockServerClient == null) {
            mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        }
    }

    @AfterEach
    public void resetMockServer() {
        mockServerClient.reset();
    }


    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("gerrit.url", () -> MessageFormat.format("http://{0}:{1}", mockServer.getHost(), mockServer.getServerPort()));
        registry.add("jenkins.uri", () -> MessageFormat.format("http://{0}:{1}", mockServer.getHost(), mockServer.getServerPort()));
    }
}
