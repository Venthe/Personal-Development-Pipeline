package eu.venthe.pipeline.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@Testcontainers
public abstract class AbstractIntegrationTest {
    @Autowired
    protected ObjectMapper mapper;

    protected static ObjectMapper yamlMapper;

    static {
        yamlMapper = new ObjectMapper(new YAMLFactory()).registerModule(new JavaTimeModule());
    }

}
