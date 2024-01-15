package eu.venthe.pipeline.orchestrator.configuration;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JacksonAutoConfiguration.class)
public class YamlMapperConfiguration {
    @Bean
    public YAMLMapper yamlMapper() {
        return new YAMLMapper.Builder(new YAMLMapper())
                .addModule(new JavaTimeModule())
                .build();
    }
}
