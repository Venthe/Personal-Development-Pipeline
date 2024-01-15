/*
package eu.venthe.pipeline.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.configuration.TestVersionControlSystem;
import eu.venthe.pipeline.orchestrator.scratch.utilities.ExpressionUtilities;
import eu.venthe.pipeline.orchestrator.scratch2.model.events.Event;
import eu.venthe.pipeline.orchestrator.scratch2.services.MainService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SystemE2ETest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ExpressionUtilities expressionUtilities;
    @Autowired
    TestVersionControlSystem testVersionControlSystem;
    @Autowired
    MainService mainService;

    @Test
    void name() {
        Event event = getEvent();
        mainService.handleEvent(event);
    }

    private Event getEvent() {
        return getEvent(Map.of());
    }

    @SneakyThrows
    private Event getEvent(Map<String, String> properties) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("workflow", ".pipeline/workflows/exampleWorkflow1.yaml");
        defaults.put("repository.ref", "main");
        defaults.put("repository.projectName", "Example-Project");

        defaults.putAll(properties);

        var event = expressionUtilities.evaluate("""
                type: workflow_dispatch
                correlationId: 455ae1e8-7303-40b0-8cda-2626f5e4ea0e
                inputs:
                  first: 1
                  second: "Two"
                workflow: "${{workflow}}"
                repository:
                  ref: "${{repository.ref}}"
                  projectName: "${{repository.projectName}}"
                sender:
                  name: Venthe
                """, defaults);

        return new Event(objectMapper, (ObjectNode) objectMapper.readTree(event));
    }
}
*/
