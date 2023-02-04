/*
package eu.venthe.pipeline.gerrit_mediator;

import eu.venthe.pipeline.gerrit_mediator.gerrit.model.Account;
import eu.venthe.pipeline.gerrit_mediator.gerrit.model.Change;
import eu.venthe.pipeline.gerrit_mediator.gerrit.model.PatchSet;
import eu.venthe.pipeline.gerrit_mediator.gerrit.model.PatchsetCreated;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Slf4j
class GerritMediatorApplicationTests extends BaseTest {

    @Test
    void contextLoads() throws InterruptedException {
        Account administratorAccount = new Account().name("Administrator").email("admin@example.com").username("admin");

        var patchSetCreated = new PatchsetCreated().uploader(administratorAccount).type("patchset-created");

        var patchSet = new PatchSet().number(10).revision("b42aa3e8a18410d7e90d2f4516768b215d421193").parents(List.of("ef79d38e14d4e1e782924f977ae96ba5251985c9")).ref("refs/changes/41/41/10").uploader(administratorAccount)
                // FIXME: To date
                .createdOn("1669310025").kind(PatchSet.KindEnum.NO_CODE_CHANGE)
                // FIXME: To int
                .sizeInsertions("10")
                // FIXME: To int
                .sizeDeletions("0");

        patchSetCreated.patchSet(patchSet);

        var change = new Change().project("Test").branch("master").id("I82e2cdfc33bab1fc92ef2b6d004c0d1905791a39").number(41).subject("Example 34345").owner(administratorAccount).url(URI.create("http://da00cc7d77f0/c/Test/+/41")).commitMessage("Example 34345\\n\\nChange-Id: I82e2cdfc33bab1fc92ef2b6d004c0d1905791a39\\n")
                // FIXME: To date;
                .createdOn("1669309009").status(Change.StatusEnum.NEW).wip(true);

        patchSetCreated.change(change)
                // FIXME: To date
                .eventCreatedOn("1669310025");
        // FIXME: Lacking project
        //  project: { name: 'Test' },
        // FIXME: Lacking refName
        //  refName: 'refs/heads/master',
        // FIXME: Lacking changeKey
        //  changeKey: { key: 'I82e2cdfc33bab1fc92ef2b6d004c0d1905791a39' },

        var exchange = application.post()
                .uri("/patchset-created")
                .header("content-type", "application/json; charset=utf-8")
                .header("x-origin-url", "http://da00cc7d77f0/")
                .header("content-length", "1063")
                .header("host", "gerrit-to-bus-mediator:8080")
                .header("connection", "Keep-Alive")
                .header("user-agent", "Apache-HttpClient/4.5.2 (Java/11.0.17)")
                .header("accept-encoding", "gzip,deflate")
                .body(Mono.just(patchSetCreated), PatchsetCreated.class)
                .exchangeToMono(responseWithBody(String.class));

        StepVerifier.create(exchange)
                .assertNext((Tuple2<ClientResponse, String> cr) -> {
                    statusCode(cr).isEqualTo(HttpStatus.OK);
                    header(cr, "Content-Type").contains("text/plain;charset=UTF-8");
                    header(cr, "Content-Length").contains("5");
                    payload(cr).isEqualTo("Works");
                })
                .expectComplete()
                .verify();
    }

    private static AbstractStringAssert<?> payload(Tuple2<ClientResponse, String> cr) {
        return Assertions.assertThat(cr.getT2());
    }

    private static ObjectAssert<HttpStatusCode> statusCode(Tuple2<ClientResponse, String> cr) {
        return Assertions.assertThat(cr.getT1().statusCode());
    }

    private static ListAssert<String> header(Tuple2<ClientResponse, String> cr, String headerName) {
        return Assertions.assertThat(cr.getT1().headers().header(headerName));
    }
}
*/
