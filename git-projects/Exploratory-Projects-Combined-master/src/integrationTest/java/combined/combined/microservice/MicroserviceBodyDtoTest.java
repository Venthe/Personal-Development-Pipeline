package eu.venthe.combined.microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MicroserviceBodyDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void name() throws JsonProcessingException {
        final MicroserviceBodyDto microserviceBodyDto = new MicroserviceBodyDto("Name", "Surname");

        final String s = objectMapper.writeValueAsString(microserviceBodyDto);
        final MicroserviceBodyDto microserviceBodyDto1 = objectMapper.readValue(s, MicroserviceBodyDto.class);

        Assertions.assertThat(microserviceBodyDto1.getName()).isEqualTo("Name");
        Assertions.assertThat(microserviceBodyDto1.getSurname()).isEqualTo("Surname");
    }
}
