package eu.venthe.pipeline.pipeline_mediator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.venthe.pipeline.pipeline_mediator.api.model.EventTriggerDto;
import eu.venthe.pipeline.pipeline_mediator.configuration.YamlMapper;
import eu.venthe.pipeline.pipeline_mediator.core.EventFactory;
import eu.venthe.pipeline.pipeline_mediator.core.model.AbstractContinuousIntegrationEvent;
import eu.venthe.pipeline.pipeline_mediator.core.model.DispatchEvent;
import eu.venthe.pipeline.pipeline_mediator.core.repository.ContinuousIntegrationEventRepository;
import eu.venthe.pipeline.pipeline_mediator.dependencies.gerrit_hook_binding.GerritApi;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

class RepositoryTest extends AbstractBaseTest {
    @Autowired
    ContinuousIntegrationEventRepository repository;
    @Autowired
    EventFactory eventFactory;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    YamlMapper yamlMapper;
    @MockBean
    GerritApi gerritApi;

    @Test
    void trigger() throws JsonProcessingException {
        Mockito.when(gerritApi.getCommitForProject(any(), any(), any())).thenReturn(objectMapper.createObjectNode());
        Mockito.when(gerritApi.getCommitFilesForProject(any(), any(), any())).thenReturn(objectMapper.createObjectNode());
        String event =
                "type: \"workflow-dispatch\"\n" +
                "projectName: \"State-Repository\"\n" +
                "branchName: \"main\"\n" +
                "workflow: update-property\n" +
                "inputs:\n" +
                "  namespace: integration\n" +
                "  applicationName: argocd-example\n" +
                "  propertyKey: test\n" +
                "  propertyValue: test6";

        EventTriggerDto trigger = yamlMapper.getObjectMapper().readValue(event, EventTriggerDto.class);
        DispatchEvent save = repository.save(DispatchEvent.create(trigger, objectMapper, gerritApi));

        repository.save(save);

        Optional<AbstractContinuousIntegrationEvent> byReferenceId = repository.findByReferenceId(save.getReferenceId());

        Assertions.assertThat(byReferenceId).isPresent();
    }
}
