package cpt204.graph;

import java.util.ArrayList;
import java.util.List;

public class GraphAlgorithmComparisonService {
    public List<GraphAlgorithmComparisonResult> compareAlgorithms(
            List<ShortestPathSolver> solvers,
            List<GraphCaseDefinition> caseDefinitions
    ) {
        List<GraphAlgorithmComparisonResult> results = new ArrayList<>();

        for (ShortestPathSolver solver : solvers) {
            GraphQueryService queryService = new GraphQueryService(solver);
            for (GraphCaseDefinition caseDefinition : caseDefinitions) {
                // Time the full case, including any required waypoints.
                long startTime = System.nanoTime();
                GraphQueryResult queryResult = queryService.queryWithOrderedWaypoints(
                        caseDefinition.getCaseName(),
                        caseDefinition.getStart(),
                        caseDefinition.getDestination(),
                        caseDefinition.getWaypointsInOrder()
                );
                long endTime = System.nanoTime();

                results.add(new GraphAlgorithmComparisonResult(
                        solver.getName(),
                        queryResult,
                        endTime - startTime
                ));
            }
        }
        return results;
    }
}
