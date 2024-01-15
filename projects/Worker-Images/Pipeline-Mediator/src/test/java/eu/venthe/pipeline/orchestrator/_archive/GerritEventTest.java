/*
package eu.venthe.pipeline.orchestrator;

import eu.venthe.pipeline.orchestrator.archive.api.model.EventTriggerDto;
import eu.venthe.pipeline.orchestrator.configuration.TestEventApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;

class GerritEventTest extends AbstractBaseTest {

    @Autowired
    TestEventApi testEventApi;

    @BeforeEach
    void beforeEach() {
        testEventApi.port = this.port;
    }

    @Test
    void roughTest() {
        // given

        testEventApi.queueEvent(
                new EventTriggerDto("patchset-created", "Example project", "xyz", "main", null, "build", null)
        );

        // when
        Awaitility.await().atLeast(Duration.ofSeconds(10));
    }
}
*/
