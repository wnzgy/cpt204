package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphQueryService {
    private final ShortestPathSolver shortestPathSolver;

    public GraphQueryService(ShortestPathSolver shortestPathSolver) {
        this.shortestPathSolver = shortestPathSolver;
    }

    public GraphQueryResult queryWithOrderedWaypoints(
            String caseName,
            String start,
            String destination,
            List<String> orderedWaypoints
    ) {
        List<String> sequence = new ArrayList<>();

        sequence.add(start);
        sequence.addAll(orderedWaypoints);
        sequence.add(destination);

        long totalCost = 0L;
        List<String> mergedPath = new ArrayList<>();

        for (int i = 0; i < sequence.size() - 1; i++) {
            String from = sequence.get(i);
            String to = sequence.get(i + 1);
            PathResult segment = shortestPathSolver.shortestPath(from, to);
            if (!segment.isReachable() || segment.getPath().isEmpty()) {
                return new GraphQueryResult(
                        caseName,
                        start,
                        destination,
                        orderedWaypoints,
                        false,
                        Long.MAX_VALUE,
                        Collections.<String>emptyList()
                );
            }
            totalCost += segment.getTotalCost();
            if (i == 0) {
                mergedPath.addAll(segment.getPath());
            } else {
                mergedPath.addAll(segment.getPath().subList(1, segment.getPath().size()));
            }
        }

        return new GraphQueryResult(
                caseName,
                start,
                destination,
                orderedWaypoints,
                true,
                totalCost,
                mergedPath
        );
    }
}
