package eu.venthe.pipeline.orchestrator.workflows.contexts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.events.EventType;
import eu.venthe.pipeline.orchestrator.events.impl.PullRequestEventContext;
import eu.venthe.pipeline.orchestrator.events.impl.PushEventContext;
import eu.venthe.pipeline.orchestrator.events.impl.WorkflowCallEventContext;
import eu.venthe.pipeline.orchestrator.events.impl.WorkflowDispatchEventContext;
import eu.venthe.pipeline.orchestrator.utilities.YamlUtility;
import eu.venthe.pipeline.orchestrator.workflows.Workflow;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static eu.venthe.pipeline.orchestrator.utilities.YamlUtility.parseYaml;

class OnContextTest {

    @Test
    void notNull1() {
        // given & when
        ThrowableAssert.ThrowingCallable action = () -> getWorkflow(null);

        // then
        Assertions.assertThatThrownBy(action).hasMessage("Workflow should not be null");
    }

    @Test
    void notNull2() {
        // given
        var workflow = getWorkflow("""
                on: ~
                """);

        // when
        ThrowableAssert.ThrowingCallable action = () -> isHandling(workflow, null);

        // then
        Assertions.assertThatThrownBy(action).hasMessage("Input should not be null");
    }

    @Test
    void noOnProperty() {
        // given & when
        ThrowableAssert.ThrowingCallable action = () -> getWorkflow("""
                any: 1
                """);;

        // then
        Assertions.assertThatThrownBy(action).hasMessage("There is no \"on\" property, this workflow will never run");
    }

    @Test
    void singleEvent() {
        // given
        var input = getEvent("""
                type: pull_request
                """);
        var workflow = getWorkflow("""
                on: pull_request
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void multipleEvents() {
        // given
        var input = getEvent("""
                type: pull_request
                """);
        var workflow = getWorkflow("""
                on: [pull_request]
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push1() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - main
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push2() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - ma*
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push3() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - lorem
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void activityType_push4() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - lorem
                      - main
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push4_1() {
        // given
        var input = getEvent("""
                type: push
                ref: refs/heads/main
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - '**main'
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push4_2() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    branches:
                      - '**main'
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push5() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                additionalProperties:
                  files:
                    "a.yaml":
                      status: A
                      lines_deleted: -5
                      size_delta: 5
                      size: 5
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    paths:
                      - '**.yaml'
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void activityType_push6() {
        // given
        var input = getEvent("""
                type: push
                ref: main
                additionalProperties:
                  files:
                    "a.yaml":
                      status: A
                      lines_deleted: -5
                      size_delta: 5
                      size: 5
                    
                """);
        var workflow = getWorkflow("""
                on:
                  push:
                    paths:
                      - '**.other'
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void workflowDispatchWithNonRequiredInput() {
        // given
        var input = getEvent("""
                type: workflow_dispatch
                """);
        var workflow = getWorkflow("""
                on:
                  workflow_dispatch:
                    inputs:
                      username:
                        description: 'A username passed from the caller workflow'
                        default: 'john-doe'
                        required: false
                        type: string
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void isHandlingDifferentTypeThanCalled() {
        // given
        var input = getEvent("""
                type: pull_request
                """);
        var workflow = getWorkflow("""
                on:
                  workflow_call:
                    inputs:
                      username:
                        description: 'A username passed from the caller workflow'
                        default: 'john-doe'
                        required: false
                        type: string
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void workflowDispatch1() {
        // given
        var input = getEvent("""
                type: workflow_dispatch
                additionalProperties:
                    workflow: build
                """);
        var workflow = getWorkflow("""
                on:
                  workflow_dispatch:
                    inputs:
                      username:
                        description: 'A username passed from the caller workflow'
                        default: 'john-doe'
                        required: false
                        type: string
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void workflowDispatch2() {
        // given
        var input = getEvent("""
                type: workflow_dispatch
                additionalProperties:
                    workflow: build2
                """);
        var workflow = getWorkflow("""
                on:
                  workflow_dispatch:
                    inputs:
                      username:
                        description: 'A username passed from the caller workflow'
                        default: 'john-doe'
                        required: false
                        type: string
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void workflowDispatch1_1() {
        // given
        var input = getEvent("""
                type: workflow_dispatch
                additionalProperties:
                    workflow: build
                """);
        var workflow = getWorkflow("""
                on:
                  workflow_dispatch: {}
                """);

        // when
        boolean result = isHandling(workflow, input);

        // then
        Assertions.assertThat(result).isTrue();
    }

    private static boolean isHandling(Workflow workflow, Event event) {
        return workflow.on(event);
    }

    @NotNull
    private static Event eventMapper(ObjectNode event) {
        EventType type = Optional.ofNullable(event.get("type"))
                .map(JsonNode::asText).flatMap(EventType::of)
                .orElseThrow(() -> new NoSuchElementException(event.get("type").asText()));
        return switch (type) {
            case WORKFLOW_DISPATCH -> new WorkflowDispatchEventContext(event);
            case WORKFLOW_CALL -> new WorkflowCallEventContext(event);
            case PUSH -> new PushEventContext(event);
            case PULL_REQUEST -> new PullRequestEventContext(event);
            default -> throw new UnsupportedOperationException();
        };
    }

    @NotNull
    private static Workflow getWorkflow(String value) {
        if (value == null) {
            return new Workflow(null, "Test");
        }
        return new Workflow(value.isBlank() ? YamlUtility.getNodeFactory().objectNode() : (ObjectNode) parseYaml(value), "TEST");
    }

    @NotNull
    private static Event getEvent(String value) {
        ObjectNode event = (ObjectNode) parseYaml(value);
        event.set("id", YamlUtility.getNodeFactory().textNode("a5b41ec4-c088-420a-93ae-8b0868b6e1b8"));
        return eventMapper(event);
    }
}
