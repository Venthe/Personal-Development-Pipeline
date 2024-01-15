package eu.venthe.pipeline.orchestrator.utilities;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class GraphUtility {
    public static List<List<String>> getGroups(Graph<String, DefaultEdge> taskGraph) {
        List<List<String>> groups = new ArrayList<>();
        //The first group contains all vertices without incoming arcs
        List<String> group = new LinkedList<>();
        for (String task : taskGraph.vertexSet())
            if (taskGraph.inDegreeOf(task) == 0) group.add(task);
        //Next we construct all remaining groups. The group k+1 consists of al vertices without incoming arcs if we were
        //to remove all vertices in the previous group k from the graph.
        var result = true;
        while (result) {
            groups.add(group);
            List<String> nextGroup = new LinkedList<>();
            for (String task : group) {
                for (String nextTask : Graphs.neighborSetOf(taskGraph, task)) {
                    if (taskGraph.inDegreeOf(nextTask) == 1) nextGroup.add(nextTask);
                }
                taskGraph.removeVertex(task); //Removes a vertex and all its edges from the graph
            }
            nextGroup = nextGroup.stream().sorted().collect(Collectors.toList());
            group = nextGroup;
            result = !group.isEmpty();
        }
        return groups;
    }

    public static List<List<String>> buildDependencyTree(Set<JobRequirements> jobRequirements) {
        Graph<String, DefaultEdge> taskGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        Graphs.addAllVertices(taskGraph, jobRequirements.stream().map(JobRequirements::jobId).collect(Collectors.toSet()));

        jobRequirements.stream()
                .filter(JobRequirements::hasNeeds)
                .flatMap(e -> e.needs().stream()
                        .map(String::trim)
                        .filter(Predicate.not(String::isBlank))
                        .map(v -> Map.entry(v, e.jobId()))
                )
                .forEach(e -> taskGraph.addEdge(e.getKey(), e.getValue()));

        return GraphUtility.getGroups(taskGraph);
    }

    public record JobRequirements(String jobId, Set<String> needs) {

        public boolean hasNeeds() {
            return !needs.isEmpty();
        }
    }
}
