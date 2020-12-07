package eu.venthe.combined.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.combined.configuration.MockServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockServerConfiguration.class})
@ActiveProfiles("test")
class HATEOASTest {

    @LocalServerPort
    private int port;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getSingle() {
        final ResponseEntity<JsonNode> result = restTemplate.getForEntity("http://localhost:" + port + "/hateoas-manager/employees/3", JsonNode.class);

        log.info("{}", Objects.requireNonNull(result.getBody()).toPrettyString());
        Assertions.assertThat(result.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @Disabled("Incorrect response")
    void post() {
        final ResponseEntity<JsonNode> result = restTemplate.postForEntity("http://localhost:" + port + "/hateoas-manager/employees", HATEOASController.TestDto.of(1, "1"), JsonNode.class);

        log.info("{}", Objects.requireNonNull(result.getBody()).toPrettyString());
        Assertions.assertThat(result.getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

//    @Test
//    @Disabled
//    void put() {
//        final ResponseEntity<JsonNode> result = restTemplate.put("http://localhost:" + port + "/hateoas-manager/employees/3", HttpMethod.PUT, HATEOASController.TestDto.of(1, "1"), JsonNode.class);
//
//        log.info("{}", Objects.requireNonNull(result.getBody()).toPrettyString());
//
//        Assertions.assertThat(result.getStatusCode())
//                .isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    @Disabled
//    void delete() {
//        final ResponseEntity<JsonNode> result = restTemplate.delete("http://localhost:" + port + "/hateoas-manager/employees/3");
//
//        log.info("{}", Objects.requireNonNull(result.getBody()).toPrettyString());
//
//        Assertions.assertThat(result.getStatusCode())
//                .isEqualTo(HttpStatus.OK);
//    }

    @Test
    void list() throws JsonProcessingException {
        final ResponseEntity<JsonNode> result = restTemplate.getForEntity("http://localhost:" + port + "/hateoas-manager/employees", JsonNode.class);

        log.info("{}", Objects.requireNonNull(result.getBody()).toPrettyString());
        Assertions.assertThat(result.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody())
                .isNotNull();
        Assertions.assertThat(result.getBody().toPrettyString())
                .isEqualTo(objectMapper.readTree("{\n" +
                        "  \"_embedded\": {\n" +
                        "    \"testDtoList\": [\n" +
                        "      {\n" +
                        "        \"value\": 1,\n" +
                        "        \"text\": \"2\",\n" +
                        "        \"_links\": {\n" +
                        "          \"self\": {\n" +
                        "            \"href\": \"http://localhost:" + port + "/hateoas-manager/employees/1\"\n" +
                        "          },\n" +
                        "          \"employees\": {\n" +
                        "            \"href\": \"http://localhost:" + port + "/hateoas-manager/employees\"\n" +
                        "          }\n" +
                        "        },\n" +
                        "        \"_templates\": {\n" +
                        "          \"default\": {\n" +
                        "            \"method\": \"put\",\n" +
                        "            \"properties\": [\n" +
                        "              {\n" +
                        "                \"name\": \"text\",\n" +
                        "                \"readOnly\": true\n" +
                        "              },\n" +
                        "              {\n" +
                        "                \"name\": \"value\",\n" +
                        "                \"readOnly\": true\n" +
                        "              }\n" +
                        "            ]\n" +
                        "          },\n" +
                        "          \"deleteEmployee\": {\n" +
                        "            \"method\": \"delete\",\n" +
                        "            \"properties\": []\n" +
                        "          }\n" +
                        "        }\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  \"_links\": {\n" +
                        "    \"self\": {\n" +
                        "      \"href\": \"http://localhost:" + port + "/hateoas-manager/employees\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"_templates\": {\n" +
                        "    \"default\": {\n" +
                        "      \"method\": \"post\",\n" +
                        "      \"properties\": [\n" +
                        "        {\n" +
                        "          \"name\": \"text\",\n" +
                        "          \"readOnly\": true\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"value\",\n" +
                        "          \"readOnly\": true\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}").toPrettyString()
                );
    }

}
