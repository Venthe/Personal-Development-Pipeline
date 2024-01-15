package eu.venthe.pipeline.orchestrator.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum EventType {
    BRANCH_PROTECTION_RULE("branch_protection_rule"),
    CHECK_RUN("check_run"),
    CHECK_SUITE("check_suite"),
    CREATE("create"),
    DELETE("delete"),
    DEPLOYMENT("deployment"),
    DEPLOYMENT_STATUS("deployment_status"),
    DISCUSSION("discussion"),
    DISCUSSION_COMMENT("discussion_comment"),
    FORK("fork"),
    GOLLUM("gollum"),
    ISSUE_COMMENT("issue_comment"),
    ISSUES("issues"),
    LABEL("label"),
    MERGE_GROUP("merge_group"),
    MILESTONE("milestone"),
    PAGE_BUILD("page_build"),
    PROJECT("project"),
    PROJECT_CARD("project_card"),
    PROJECT_COLUMN("project_column"),
    PUBLIC("public"),
    PULL_REQUEST("pull_request"),
    @Deprecated
    PULL_REQUEST_COMMENT("pull_request_comment"),
    PULL_REQUEST_REVIEW("pull_request_review"),
    PULL_REQUEST_REVIEW_COMMENT("pull_request_review_comment"),
    PULL_REQUEST_TARGET("pull_request_target"),
    PUSH("push"),
    REGISTRY_PACKAGE("registry_package"),
    RELEASE("release"),
    REPOSITORY_DISPATCH("repository_dispatch"),
    SCHEDULE("schedule"),
    STATUS("status"),
    WATCH("watch"),
    WORKFLOW_CALL("workflow_call"),
    WORKFLOW_DISPATCH("workflow_dispatch"),
    WORKFLOW_RUN("workflow_run");

    private final String value;

    public static Optional<EventType> of(String type) {
        if (type == null) {
            return Optional.empty();
        }

        return stream(values())
                .filter(eventType -> eventType.getValue().equalsIgnoreCase(type.trim()))
                .collect(toOptional());
    }

    public static final Set<EventType> EVENTS_WITH_ACTION = Set.of(
            BRANCH_PROTECTION_RULE,
            CHECK_RUN,
            CHECK_SUITE,
            DISCUSSION,
            DISCUSSION_COMMENT,
            ISSUE_COMMENT,
            ISSUES,
            LABEL,
            MERGE_GROUP,
            MILESTONE,
            PROJECT,
            PROJECT_CARD,
            PROJECT_COLUMN,
            PULL_REQUEST,
            PULL_REQUEST_COMMENT,
            PULL_REQUEST_REVIEW,
            PULL_REQUEST_REVIEW_COMMENT,
            PULL_REQUEST_TARGET,
            REGISTRY_PACKAGE,
            RELEASE,
            REPOSITORY_DISPATCH,
            WATCH,
            WORKFLOW_RUN
    );
}
