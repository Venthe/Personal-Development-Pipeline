package eu.venthe.pipeline.pipeline_mediator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class YamlMapper {
    @Getter
    private final ObjectMapper objectMapper;

    public YamlMapper() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;
    }
}
