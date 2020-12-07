package eu.venthe.combined.api;

import eu.venthe.combined.configuration.MockServerConfiguration;
import eu.venthe.combined.kafka.KafkaListenerService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockServerConfiguration.class})
@ActiveProfiles("test")
class KafkaControllerTest {

    static final int COUNT = 100;

    @LocalServerPort
    private int port;

    @Autowired
    RestTemplate restTemplate;

    @MockBean
    KafkaListenerService kafkaListenerService;

    @Autowired
    ConsumerFactory<?, ?> consumerFactory;

    CountDownLatch countDownLatch;

    @BeforeEach
    void prepare() {
        countDownLatch = new CountDownLatch(COUNT);
        Mockito.doAnswer(invocation -> {
            log.info("Current invocation: {}", invocation.getArgument(0).toString());
            countDownLatch.countDown();
            return null;
        }).when(kafkaListenerService).act(any());
    }

    @Test
    @Disabled("Incorrect approach")
    void test() throws InterruptedException {
        restTemplate.postForLocation("http://localhost:" + port + "/kafka-manager/pause", null);
        for (var i = COUNT; i > 0; i--)
            restTemplate.postForLocation("http://localhost:" + port + "/kafka-manager/send/" + i, null);
        restTemplate.postForLocation("http://localhost:" + port + "/kafka-manager/resume", null);

        countDownLatch.await(60, TimeUnit.SECONDS);

        Assertions.assertThat(countDownLatch.getCount()).isZero();
    }
}
