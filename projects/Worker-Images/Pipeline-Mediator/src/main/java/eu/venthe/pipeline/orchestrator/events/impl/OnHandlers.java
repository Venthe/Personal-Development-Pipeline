package eu.venthe.pipeline.orchestrator.events.impl;

import eu.venthe.pipeline.orchestrator.events.Event;
import eu.venthe.pipeline.orchestrator.utilities.GlobPatternMatching;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextBranches;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextPaths;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextInputs;
import eu.venthe.pipeline.orchestrator.workflows.contexts.OnContextTypes;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
@Slf4j
public class OnHandlers {
    public static boolean typesMatch(OnContextTypes onContextTypes, Event event, Supplier<String> actionSupplier) {
        List<Boolean> votes = new ArrayList<>();

        String action = actionSupplier.get();

        onContextTypes.text()
                .ifPresent(text -> votes.add(text.equalsIgnoreCase(action)));
        onContextTypes.array()
                .ifPresent(arr -> arr.stream().map(v -> v.equalsIgnoreCase(action)).forEach(votes::add));

        boolean result = votes.isEmpty() || votes.stream().allMatch(e -> e.equals(true));
        log.info("{} matching action - {}", event.getType(), result);
        return result;
    }

    public static boolean inputsMatch(OnContextInputs onContextInputs, Event event, Supplier<List<String>> inputsSupplier) {
        // TODO: Handle type of the input
        List<String> inputs = inputsSupplier.get();
        boolean result = onContextInputs.requiredInputs().stream()
                .allMatch(a -> inputs.stream().anyMatch(e -> e.equalsIgnoreCase(a.getKey())));
        log.info("{} matching inputs - {}", event.getType(), result);
        return result;
    }

    public static boolean branchesMatch(OnContextBranches onContextBranches, Event event, Supplier<String> branchSupplier) {
        List<Boolean> votes = new ArrayList<>();

        String branch = branchSupplier.get();

        votes.add(onContextBranches.branches().stream().anyMatch(b -> GlobPatternMatching.isMatching(b, branch)) || onContextBranches.branches().isEmpty());
        votes.add(onContextBranches.branchesIgnore().stream().noneMatch(b -> GlobPatternMatching.isMatching(b, branch)) || onContextBranches.branches().isEmpty());

        boolean result = votes.stream().allMatch(e -> e.equals(true));
        log.info("{} matching branches - {}", event.getType(), result);
        return result;
    }

    public static boolean pathsMatch(OnContextPaths onContextBranches, Event event, Supplier<List<String>> pathsSupplier) {
        List<Boolean> votes = new ArrayList<>();

        List<String> paths = pathsSupplier.get();

        Predicate<String> match = pattern -> paths.stream().map(path -> GlobPatternMatching.isMatching(pattern, path)).anyMatch(e->e.equals(true));

        votes.add(onContextBranches.paths().stream().anyMatch(match) || onContextBranches.paths().isEmpty());
        votes.add(onContextBranches.pathsIgnore().stream().noneMatch(match) || onContextBranches.pathsIgnore().isEmpty());

        boolean result = votes.stream().allMatch(e -> e.equals(true));
        log.info("{} matching paths - {}", event.getType(), result);
        return result;
    }
}
