/*
package eu.venthe.pipeline.gerrit_mediator;

import eu.venthe.pipeline.gerrit_mediator.jenkins.api.DefaultApi;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

public class JenkinsTest extends BaseTest {
    @Autowired
    JenkinsConfiguration.Api api;

    @Test
    void name() throws IOException {
        String fileContent = """
                abd:
                    aasb: 2
                """;
        ResponseEntity<Void> result = api.jobJobNameBuildWithParametersPostWithHttpInfo(fileContent).log().block();

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
    }
}
*/
