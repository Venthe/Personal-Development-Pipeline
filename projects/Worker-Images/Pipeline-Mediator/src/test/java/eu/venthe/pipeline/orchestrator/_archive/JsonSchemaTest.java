/*
package eu.venthe.pipeline.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class JsonSchemaTest {
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    @Test
    void name() throws IOException {
        SchemaRepository repository =
                SchemaRepository.create(new JsonSchemaOptions().setBaseUri("https://vertx.io"));

        String schema = loadYaml("./schema/exampleSchema.yaml").toPrettyString();
        String json1 = loadYaml("./schema/test.yaml").toPrettyString();
        String json2 = loadYaml("./schema/test2.yaml").toPrettyString();

        JsonSchema schema_ = JsonSchema.of(new JsonObject(schema));

        OutputUnit validate1 = repository.validator(schema_, new JsonSchemaOptions().setDraft(Draft.DRAFT202012)).validate(new JsonObject(json1));
        OutputUnit validate2 = repository.validator(schema_, new JsonSchemaOptions().setDraft(Draft.DRAFT202012)).validate(new JsonObject(json2));
//        OutputUnit validate3 = Validator.create(schema_, new JsonSchemaOptions().setDraft(Draft.DRAFT202012)).validate(new JsonObject(json1));
//        OutputUnit validate4 = Validator.create(schema_, new JsonSchemaOptions().setDraft(Draft.DRAFT202012)).validate(new JsonObject(json2));

        System.out.println();
    }

    private JsonNode loadYaml(String path) throws IOException {
        String pathWithRoot = Path.of(path).toString();
        URL url = ClassLoader.getSystemResource(pathWithRoot);
        return objectMapper.readTree(url);
    }
}
*/
