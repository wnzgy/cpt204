# Chapter 5 - Program Code

All Java source files are included below as plain text.

## src/cpt204/app/ExperimentOutputWriter.java

```java
package cpt204.app;

import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphQueryResult;
import cpt204.model.CandidateLocation;
import cpt204.sort.DataStructureComparisonResult;
import cpt204.sort.DatasetCharacteristicsResult;
import cpt204.sort.DatasetProfile;
import cpt204.sort.DatasetSortingResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExperimentOutputWriter {
    public void writeAll(
            Path outputDir,
            List<DatasetSortingResult> sortingResults,
            Map<String, List<CandidateLocation>> selectedByDataset,
            List<GraphQueryResult> graphResults,
            List<GraphAlgorithmComparisonResult> graphComparisonResults,
            List<DatasetCharacteristicsResult> datasetCharacteristicsResults,
            List<DataStructureComparisonResult> dataStructureComparisonResults
    ) throws IOException {
        Files.createDirectories(outputDir);
        writeSortingBenchmark(outputDir.resolve("sorting_benchmark.csv"), sortingResults);
        writeDatasetProfiles(outputDir.resolve("dataset_profiles.csv"), sortingResults);
        writeDatasetCharacteristics(outputDir.resolve("dataset_characteristics_core.csv"), datasetCharacteristicsResults);
        writeDataStructureComparison(outputDir.resolve("data_structure_comparison.csv"), dataStructureComparisonResults);
        writeSelectedLocations(outputDir.resolve("selected_locations.csv"), selectedByDataset);
        writeGraphCases(outputDir.resolve("graph_cases.csv"), graphResults);
        writeGraphAlgorithmComparison(outputDir.resolve("graph_algorithm_comparison.csv"), graphComparisonResults);
    }

    private void writeSortingBenchmark(Path file, List<DatasetSortingResult> sortingResults) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,algorithm,avg_runtime_ns,avg_runtime_ms\n");

        // Save the average running time of each sorting algorithm.
        for (DatasetSortingResult result : sortingResults) {
            for (Map.Entry<String, Long> entry : result.getAvgRuntimeByAlgorithmNanos().entrySet()) {
                long nanos = entry.getValue();
                double millis = nanos / 1_000_000.0;
                builder.append(result.getDatasetName()).append(",")
                        .append(entry.getKey()).append(",")
                        .append(nanos).append(",")
                        .append(String.format("%.6f", millis))
                        .append("\n");
            }
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeDatasetProfiles(Path file, List<DatasetSortingResult> sortingResults) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,total_rows,unique_scores,tie_score_groups,already_sorted_by_rule\n");
        for (DatasetSortingResult result : sortingResults) {
            DatasetProfile profile = result.getProfile();
            builder.append(result.getDatasetName()).append(",")
                    .append(profile.getTotalRows()).append(",")
                    .append(profile.getUniqueScoreCount()).append(",")
                    .append(profile.getTieScoreGroupCount()).append(",")
                    .append(profile.isAlreadySortedByRankingRule())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeDatasetCharacteristics(
            Path file,
            List<DatasetCharacteristicsResult> results
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,data_characteristic,total_inversions,bubble_passes,bubble_swaps,quick_sort_pivot_used,pivot_id,pivot_rank,first_partition_left,first_partition_right,pivot_quality\n");

        // Save the extra dataset analysis used to explain the sorting timings.
        for (DatasetCharacteristicsResult result : results) {
            builder.append(result.getDatasetName()).append(",")
                    .append(result.getDataCharacteristic()).append(",")
                    .append(result.getTotalInversions()).append(",")
                    .append(result.getBubblePasses()).append(",")
                    .append(result.getBubbleSwaps()).append(",")
                    .append("Last element").append(",")
                    .append(result.getPivotId()).append(",")
                    .append(result.getPivotRank()).append(",")
                    .append(result.getFirstPartitionLeft()).append(",")
                    .append(result.getFirstPartitionRight()).append(",")
                    .append(result.getPivotQuality())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeDataStructureComparison(
            Path file,
            List<DataStructureComparisonResult> results
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,list_type,algorithm,sample_size,avg_runtime_ns,avg_runtime_ms\n");

        // This supports the Task C explanation of why the chosen data structure matters.
        for (DataStructureComparisonResult result : results) {
            builder.append(result.getDatasetName()).append(",")
                    .append(result.getListType()).append(",")
                    .append(result.getAlgorithmName()).append(",")
                    .append(result.getSampleSize()).append(",")
                    .append(result.getAvgRuntimeNanos()).append(",")
                    .append(String.format("%.6f", result.getAvgRuntimeMillis()))
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeSelectedLocations(Path file, Map<String, List<CandidateLocation>> selectedByDataset) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,rank,location_id,priority_score\n");

        // Save the top selected locations so they can be copied into the report.
        for (Map.Entry<String, List<CandidateLocation>> entry : selectedByDataset.entrySet()) {
            String dataset = entry.getKey();
            List<CandidateLocation> candidates = entry.getValue();
            for (int i = 0; i < candidates.size(); i++) {
                CandidateLocation candidate = candidates.get(i);
                builder.append(dataset).append(",")
                        .append(i + 1).append(",")
                        .append(candidate.getLocationId()).append(",")
                        .append(candidate.getPriorityScore())
                        .append("\n");
            }
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeGraphCases(Path file, List<GraphQueryResult> graphResults) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("case,start,destination,waypoints_in_order,reachable,total_cost,path\n");

        // Save all required Task B cases in one table.
        for (GraphQueryResult result : graphResults) {
            String waypoints = result.getWaypointsInOrder().isEmpty() ? "NONE" : String.join(" -> ", result.getWaypointsInOrder());
            String cost = result.isReachable() ? String.valueOf(result.getTotalCost()) : "INF";
            builder.append(result.getCaseName()).append(",")
                    .append(result.getStart()).append(",")
                    .append(result.getDestination()).append(",")
                    .append(waypoints).append(",")
                    .append(result.isReachable()).append(",")
                    .append(cost).append(",")
                    .append(result.formatPath())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeGraphAlgorithmComparison(
            Path file,
            List<GraphAlgorithmComparisonResult> graphComparisonResults
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("algorithm,case,start,destination,waypoints_in_order,reachable,total_cost,runtime_ns,runtime_ms,path\n");

        // Save the comparison between Dijkstra, bidirectional Dijkstra, and A*.
        for (GraphAlgorithmComparisonResult comparisonResult : graphComparisonResults) {
            GraphQueryResult result = comparisonResult.getQueryResult();
            String waypoints = result.getWaypointsInOrder().isEmpty() ? "NONE" : String.join(" -> ", result.getWaypointsInOrder());
            String cost = result.isReachable() ? String.valueOf(result.getTotalCost()) : "INF";
            builder.append(comparisonResult.getAlgorithmName()).append(",")
                    .append(result.getCaseName()).append(",")
                    .append(result.getStart()).append(",")
                    .append(result.getDestination()).append(",")
                    .append(waypoints).append(",")
                    .append(result.isReachable()).append(",")
                    .append(cost).append(",")
                    .append(comparisonResult.getRuntimeNanos()).append(",")
                    .append(String.format("%.6f", comparisonResult.getRuntimeMillis())).append(",")
                    .append(result.formatPath())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}

```

## src/cpt204/app/UrbanInspectionApp.java

```java
package cpt204.app;

import cpt204.graph.AStarSolver;
import cpt204.graph.BidirectionalDijkstraSolver;
import cpt204.graph.DijkstraSolver;
import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphAlgorithmComparisonService;
import cpt204.graph.GraphCaseDefinition;
import cpt204.graph.GraphQueryResult;
import cpt204.graph.GraphQueryService;
import cpt204.graph.ShortestPathSolver;
import cpt204.graph.WeightedGraph;
import cpt204.io.CsvDataLoader;
import cpt204.model.CandidateLocation;
import cpt204.sort.BubbleSortStrategy;
import cpt204.sort.DataStructureComparisonResult;
import cpt204.sort.DataStructureComparisonService;
import cpt204.sort.DatasetCharacteristicsResult;
import cpt204.sort.DatasetCharacteristicsService;
import cpt204.sort.DatasetSortingResult;
import cpt204.sort.MergeSortStrategy;
import cpt204.sort.QuickSortStrategy;
import cpt204.sort.SortStrategy;
import cpt204.sort.SortingService;
import cpt204.visualizer.GraphOutputVisualizer;
import cpt204.visualizer.SortingOutputVisualizer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UrbanInspectionApp {
    private static final Path DATA_DIR = Paths.get("Group Project Datasets");
    private static final Path OUTPUT_ROOT_DIR = Paths.get("outputs");
    private static final int TOP_N = 10;
    private static final int BENCHMARK_RUNS = 3;

    public static void main(String[] args) throws IOException {
        CsvDataLoader loader = new CsvDataLoader();

        Map<String, List<CandidateLocation>> datasets = new LinkedHashMap<>();
        datasets.put("Dataset A", loader.readCandidates(DATA_DIR.resolve("candidates_A.csv")));
        datasets.put("Dataset B", loader.readCandidates(DATA_DIR.resolve("candidates_B.csv")));
        datasets.put("Dataset C", loader.readCandidates(DATA_DIR.resolve("candidates_C.csv")));

        // Three sorting methods required by Task A.
        List<SortStrategy> strategies = Arrays.asList(
                new BubbleSortStrategy(),
                new QuickSortStrategy(),
                new MergeSortStrategy()
        );
        SortingService sortingService = new SortingService(strategies, TOP_N, BENCHMARK_RUNS);

        List<DatasetSortingResult> sortingResults = new ArrayList<>();
        List<DatasetCharacteristicsResult> datasetCharacteristicsResults = new ArrayList<>();
        List<DataStructureComparisonResult> dataStructureComparisonResults = new ArrayList<>();
        Map<String, List<CandidateLocation>> selectedByDataset = new LinkedHashMap<>();
        DatasetCharacteristicsService characteristicsService = new DatasetCharacteristicsService();
        DataStructureComparisonService dataStructureService = new DataStructureComparisonService();

        for (Map.Entry<String, List<CandidateLocation>> entry : datasets.entrySet()) {
            DatasetSortingResult result = sortingService.evaluateDataset(entry.getKey(), entry.getValue());
            sortingResults.add(result);
            datasetCharacteristicsResults.add(characteristicsService.analyze(entry.getKey(), entry.getValue()));
            dataStructureComparisonResults.addAll(dataStructureService.compareQuickSort(entry.getKey(), entry.getValue()));
            selectedByDataset.put(entry.getKey(), result.getTopSelected());
        }

        WeightedGraph graph = loader.readUndirectedWeightedGraph(DATA_DIR.resolve("paths.csv"));
        DijkstraSolver solver = new DijkstraSolver(graph);
        GraphQueryService graphQueryService = new GraphQueryService(solver);

        List<CandidateLocation> topA = selectedByDataset.get("Dataset A");
        List<CandidateLocation> topB = selectedByDataset.get("Dataset B");
        List<CandidateLocation> topC = selectedByDataset.get("Dataset C");

        String a1 = topA.get(0).getLocationId();
        String a10 = topA.get(9).getLocationId();
        String b1 = topB.get(0).getLocationId();
        String b5 = topB.get(4).getLocationId();
        String c1 = topC.get(0).getLocationId();
        String c5 = topC.get(4).getLocationId();

        // The four graph cases are exactly the cases listed in Task B.
        List<GraphCaseDefinition> graphCaseDefinitions = new ArrayList<>();
        graphCaseDefinitions.add(new GraphCaseDefinition("Case 1", a1, a1, Collections.<String>emptyList()));
        graphCaseDefinitions.add(new GraphCaseDefinition("Case 2", a1, a10, Collections.<String>emptyList()));
        graphCaseDefinitions.add(new GraphCaseDefinition("Case 3", a1, b1, Collections.singletonList(b5)));
        graphCaseDefinitions.add(new GraphCaseDefinition("Case 4", a1, c1, Arrays.asList(b5, c5)));

        List<GraphQueryResult> graphResults = new ArrayList<>();
        for (GraphCaseDefinition caseDefinition : graphCaseDefinitions) {
            graphResults.add(graphQueryService.queryWithOrderedWaypoints(
                    caseDefinition.getCaseName(),
                    caseDefinition.getStart(),
                    caseDefinition.getDestination(),
                    caseDefinition.getWaypointsInOrder()
            ));
        }

        List<ShortestPathSolver> graphSolvers = Arrays.asList(
                new DijkstraSolver(graph),
                new BidirectionalDijkstraSolver(graph),
                new AStarSolver(graph)
        );
        GraphAlgorithmComparisonService comparisonService = new GraphAlgorithmComparisonService();
        List<GraphAlgorithmComparisonResult> graphComparisonResults =
                comparisonService.compareAlgorithms(graphSolvers, graphCaseDefinitions);

        printSortingSummary(sortingResults);
        printDatasetCharacteristics(datasetCharacteristicsResults);
        printDataStructureComparison(dataStructureComparisonResults);
        printSelectedLocations(selectedByDataset);
        printGraphSummary(graph, graphResults);
        printGraphAlgorithmComparison(graphComparisonResults);

        ExperimentOutputWriter outputWriter = new ExperimentOutputWriter();
        Path outputDir = createOutputDirForThisRun();
        outputWriter.writeAll(
                outputDir,
                sortingResults,
                selectedByDataset,
                graphResults,
                graphComparisonResults,
                datasetCharacteristicsResults,
                dataStructureComparisonResults
        );

        GraphOutputVisualizer visualizer = new GraphOutputVisualizer();
        visualizer.writePathSummary(outputDir.resolve("graph_path_visualization.png"), graphResults);
        visualizer.writeAlgorithmComparison(outputDir.resolve("graph_algorithm_comparison.png"), graphComparisonResults);

        SortingOutputVisualizer sortingVisualizer = new SortingOutputVisualizer();
        sortingVisualizer.writeSortingRuntimeChart(outputDir.resolve("sorting_runtime_chart.png"), sortingResults);
        sortingVisualizer.writeDatasetCharacteristicsChart(outputDir.resolve("dataset_characteristics_chart.png"), datasetCharacteristicsResults);
        sortingVisualizer.writeDataStructureChart(outputDir.resolve("data_structure_comparison_chart.png"), dataStructureComparisonResults);

        System.out.println();
        System.out.println("All experiment outputs have been written to: " + outputDir.toAbsolutePath());
    }

    private static Path createOutputDirForThisRun() {
        String timeText = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        return OUTPUT_ROOT_DIR.resolve("run_" + timeText);
    }

    private static void printSortingSummary(List<DatasetSortingResult> sortingResults) {
        System.out.println("========== Task A: Sorting Benchmark ==========");
        for (DatasetSortingResult result : sortingResults) {
            System.out.println(result.getDatasetName() + ":");
            System.out.println("  profile -> totalRows=" + result.getProfile().getTotalRows()
                    + ", uniqueScores=" + result.getProfile().getUniqueScoreCount()
                    + ", tieScoreGroups=" + result.getProfile().getTieScoreGroupCount()
                    + ", alreadySortedByRule=" + result.getProfile().isAlreadySortedByRankingRule());
            for (Map.Entry<String, Long> runtimeEntry : result.getAvgRuntimeByAlgorithmNanos().entrySet()) {
                long nanos = runtimeEntry.getValue();
                double millis = nanos / 1_000_000.0;
                System.out.printf("  %-11s avg = %d ns (%.6f ms)%n", runtimeEntry.getKey(), nanos, millis);
            }
            System.out.println();
        }
    }

    private static void printDatasetCharacteristics(List<DatasetCharacteristicsResult> results) {
        System.out.println("========== Task A: Dataset Characteristics ==========");
        for (DatasetCharacteristicsResult result : results) {
            System.out.println(result.getDatasetName() + ":");
            System.out.println("  characteristic=" + result.getDataCharacteristic());
            System.out.println("  inversions=" + result.getTotalInversions()
                    + ", bubblePasses=" + result.getBubblePasses()
                    + ", bubbleSwaps=" + result.getBubbleSwaps());
            System.out.println("  quickSortLastPivot=" + result.getPivotId()
                    + ", firstPartition=" + result.getFirstPartitionLeft()
                    + " left / " + result.getFirstPartitionRight()
                    + " right, quality=" + result.getPivotQuality());
            System.out.println();
        }
    }

    private static void printDataStructureComparison(List<DataStructureComparisonResult> results) {
        System.out.println("========== Task C: Data Structure Comparison ==========");
        for (DataStructureComparisonResult result : results) {
            System.out.printf(
                    "%s | %s | sample=%d | %s avg=%d ns (%.6f ms)%n",
                    result.getDatasetName(),
                    result.getListType(),
                    result.getSampleSize(),
                    result.getAlgorithmName(),
                    result.getAvgRuntimeNanos(),
                    result.getAvgRuntimeMillis()
            );
        }
        System.out.println();
    }

    private static void printSelectedLocations(Map<String, List<CandidateLocation>> selectedByDataset) {
        System.out.println("========== Top 10 Selected Locations ==========");
        for (Map.Entry<String, List<CandidateLocation>> entry : selectedByDataset.entrySet()) {
            System.out.println(entry.getKey() + ":");
            List<CandidateLocation> selected = entry.getValue();
            for (int i = 0; i < selected.size(); i++) {
                CandidateLocation location = selected.get(i);
                System.out.printf("  #%02d %s (score=%d)%n", i + 1, location.getLocationId(), location.getPriorityScore());
            }
            System.out.println();
        }
    }

    private static void printGraphSummary(WeightedGraph graph, List<GraphQueryResult> graphResults) {
        System.out.println("========== Task B: Graph Queries ==========");
        System.out.println("Graph summary: nodes=" + graph.nodes().size() + ", undirectedEdges=" + graph.getUndirectedEdgeCount());
        for (GraphQueryResult result : graphResults) {
            System.out.println(result.getCaseName() + ":");
            System.out.println("  start=" + result.getStart() + ", destination=" + result.getDestination());
            if (result.getWaypointsInOrder().isEmpty()) {
                System.out.println("  waypoints=NONE");
            } else {
                System.out.println("  waypoints=" + String.join(" -> ", result.getWaypointsInOrder()));
            }
            if (result.isReachable()) {
                System.out.println("  shortestPath=" + result.formatPath());
                System.out.println("  totalCost=" + result.getTotalCost());
            } else {
                System.out.println("  shortestPath=UNREACHABLE");
                System.out.println("  totalCost=INF");
            }
            System.out.println();
        }
    }

    private static void printGraphAlgorithmComparison(List<GraphAlgorithmComparisonResult> comparisonResults) {
        System.out.println("========== Task B: Graph Algorithm Comparison ==========");
        for (GraphAlgorithmComparisonResult comparisonResult : comparisonResults) {
            GraphQueryResult queryResult = comparisonResult.getQueryResult();
            String cost = queryResult.isReachable() ? String.valueOf(queryResult.getTotalCost()) : "INF";
            System.out.printf(
                    "%s | %s | cost=%s | runtime=%d ns (%.6f ms)%n",
                    comparisonResult.getAlgorithmName(),
                    queryResult.getCaseName(),
                    cost,
                    comparisonResult.getRuntimeNanos(),
                    comparisonResult.getRuntimeMillis()
            );
        }
        System.out.println("Note: A* uses h=0 because the dataset does not provide node coordinates.");
        System.out.println();
    }
}

```

## src/cpt204/graph/AStarSolver.java

```java
package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AStarSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public AStarSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "A* (h=0)";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> costFromStart = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        PriorityQueue<NodeScore> waitingNodes = new PriorityQueue<>(new Comparator<NodeScore>() {
            @Override
            public int compare(NodeScore first, NodeScore second) {
                return Long.compare(first.getTotalScore(), second.getTotalScore());
            }
        });

        costFromStart.put(start, 0L);
        waitingNodes.add(new NodeScore(start, 0L, heuristic(start, destination)));

        // A* chooses the node with the smallest g + h score.
        while (!waitingNodes.isEmpty()) {
            NodeScore current = waitingNodes.poll();
            long knownCost = costFromStart.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getCostFromStart() > knownCost) {
                continue;
            }
            if (current.getNode().equals(destination)) {
                break;
            }

            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                long newCost = current.getCostFromStart() + edge.getWeight();
                long oldCost = costFromStart.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newCost < oldCost) {
                    // A better route to this neighbor was found.
                    costFromStart.put(edge.getTo(), newCost);
                    previousNode.put(edge.getTo(), current.getNode());
                    waitingNodes.add(new NodeScore(edge.getTo(), newCost, newCost + heuristic(edge.getTo(), destination)));
                }
            }
        }

        if (!costFromStart.containsKey(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }
        return new PathResult(true, costFromStart.get(destination), buildPath(previousNode, start, destination));
    }

    private long heuristic(String current, String destination) {
        // No coordinates are provided in paths.csv, so h=0 keeps A* correct.
        return 0L;
    }

    private List<String> buildPath(Map<String, String> previousNode, String start, String destination) {
        List<String> reversedPath = new ArrayList<>();
        String node = destination;
        reversedPath.add(node);
        while (!node.equals(start)) {
            node = previousNode.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            reversedPath.add(node);
        }

        Collections.reverse(reversedPath);
        return reversedPath;
    }

    private static class NodeScore {
        private final String node;
        private final long costFromStart;
        private final long totalScore;

        private NodeScore(String node, long costFromStart, long totalScore) {
            this.node = node;
            this.costFromStart = costFromStart;
            this.totalScore = totalScore;
        }

        private String getNode() {
            return node;
        }

        private long getCostFromStart() {
            return costFromStart;
        }

        private long getTotalScore() {
            return totalScore;
        }
    }
}

```

## src/cpt204/graph/BidirectionalDijkstraSolver.java

```java
package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class BidirectionalDijkstraSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public BidirectionalDijkstraSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Bidirectional Dijkstra";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> distanceFromStart = new HashMap<>();
        Map<String, Long> distanceFromEnd = new HashMap<>();
        Map<String, String> previousFromStart = new HashMap<>();
        Map<String, String> nextToEnd = new HashMap<>();

        PriorityQueue<NodeDistance> startQueue = createQueue();
        PriorityQueue<NodeDistance> endQueue = createQueue();

        distanceFromStart.put(start, 0L);
        distanceFromEnd.put(destination, 0L);
        startQueue.add(new NodeDistance(start, 0L));
        endQueue.add(new NodeDistance(destination, 0L));

        long bestCost = Long.MAX_VALUE;
        String meetingNode = null;

        // Search from both ends and stop when no shorter connection is possible.
        while (!startQueue.isEmpty() && !endQueue.isEmpty()) {
            if (bestCost != Long.MAX_VALUE
                    && startQueue.peek().getDistance() + endQueue.peek().getDistance() >= bestCost) {
                break;
            }

            SearchResult startResult = expandOneSide(startQueue, distanceFromStart, previousFromStart, distanceFromEnd);
            if (startResult.getCost() < bestCost) {
                bestCost = startResult.getCost();
                meetingNode = startResult.getMeetingNode();
            }

            SearchResult endResult = expandOneSide(endQueue, distanceFromEnd, nextToEnd, distanceFromStart);
            if (endResult.getCost() < bestCost) {
                bestCost = endResult.getCost();
                meetingNode = endResult.getMeetingNode();
            }
        }

        if (meetingNode == null) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }
        return new PathResult(true, bestCost, buildPath(start, destination, meetingNode, previousFromStart, nextToEnd));
    }

    private PriorityQueue<NodeDistance> createQueue() {
        return new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });
    }

    private SearchResult expandOneSide(
            PriorityQueue<NodeDistance> queue,
            Map<String, Long> thisDistance,
            Map<String, String> thisPrevious,
            Map<String, Long> otherDistance
    ) {
        NodeDistance current = queue.poll();
        long knownDistance = thisDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
        if (current.getDistance() > knownDistance) {
            return new SearchResult(null, Long.MAX_VALUE);
        }

        String bestMeetingNode = null;
        long bestCost = Long.MAX_VALUE;

        // If this side reaches a node already reached by the other side, paths can meet here.
        if (otherDistance.containsKey(current.getNode())) {
            bestMeetingNode = current.getNode();
            bestCost = current.getDistance() + otherDistance.get(current.getNode());
        }

        for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
            long newDistance = current.getDistance() + edge.getWeight();
            long oldDistance = thisDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
            if (newDistance < oldDistance) {
                thisDistance.put(edge.getTo(), newDistance);
                thisPrevious.put(edge.getTo(), current.getNode());
                queue.add(new NodeDistance(edge.getTo(), newDistance));
            }

            if (otherDistance.containsKey(edge.getTo())) {
                long fullCost = newDistance + otherDistance.get(edge.getTo());
                if (fullCost < bestCost) {
                    bestCost = fullCost;
                    bestMeetingNode = edge.getTo();
                }
            }
        }
        return new SearchResult(bestMeetingNode, bestCost);
    }

    private List<String> buildPath(
            String start,
            String destination,
            String meetingNode,
            Map<String, String> previousFromStart,
            Map<String, String> nextToEnd
    ) {
        List<String> leftPart = new ArrayList<>();
        String node = meetingNode;
        leftPart.add(node);
        while (!node.equals(start)) {
            // Move backwards through the start-side search tree.
            node = previousFromStart.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            leftPart.add(node);
        }
        Collections.reverse(leftPart);

        List<String> fullPath = new ArrayList<>(leftPart);
        node = meetingNode;
        while (!node.equals(destination)) {
            // Move forwards through the end-side search tree.
            node = nextToEnd.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            fullPath.add(node);
        }
        return fullPath;
    }

    private static class NodeDistance {
        private final String node;
        private final long distance;

        private NodeDistance(String node, long distance) {
            this.node = node;
            this.distance = distance;
        }

        private String getNode() {
            return node;
        }

        private long getDistance() {
            return distance;
        }
    }

    private static class SearchResult {
        private final String meetingNode;
        private final long cost;

        private SearchResult(String meetingNode, long cost) {
            this.meetingNode = meetingNode;
            this.cost = cost;
        }

        private String getMeetingNode() {
            return meetingNode;
        }

        private long getCost() {
            return cost;
        }
    }
}

```

## src/cpt204/graph/DijkstraSolver.java

```java
package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraSolver implements ShortestPathSolver {
    private final WeightedGraph graph;

    public DijkstraSolver(WeightedGraph graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }

    @Override
    public PathResult shortestPath(String start, String destination) {
        if (start.equals(destination)) {
            return new PathResult(true, 0L, Collections.singletonList(start));
        }
        if (!graph.nodes().contains(start) || !graph.nodes().contains(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        Map<String, Long> shortestDistance = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        PriorityQueue<NodeDistance> waitingNodes = new PriorityQueue<>(new Comparator<NodeDistance>() {
            @Override
            public int compare(NodeDistance first, NodeDistance second) {
                return Long.compare(first.getDistance(), second.getDistance());
            }
        });

        shortestDistance.put(start, 0L);
        waitingNodes.add(new NodeDistance(start, 0L));

        // Dijkstra always expands the not-yet-processed node with the smallest distance.
        while (!waitingNodes.isEmpty()) {
            NodeDistance current = waitingNodes.poll();
            long knownDistance = shortestDistance.getOrDefault(current.getNode(), Long.MAX_VALUE);
            if (current.getDistance() > knownDistance) {
                continue;
            }
            if (current.getNode().equals(destination)) {
                break;
            }

            // Try to improve the shortest known distance of each neighbor.
            for (WeightedGraph.Edge edge : graph.neighborsOf(current.getNode())) {
                long newDistance = current.getDistance() + edge.getWeight();
                long oldDistance = shortestDistance.getOrDefault(edge.getTo(), Long.MAX_VALUE);
                if (newDistance < oldDistance) {
                    shortestDistance.put(edge.getTo(), newDistance);
                    previousNode.put(edge.getTo(), current.getNode());
                    waitingNodes.add(new NodeDistance(edge.getTo(), newDistance));
                }
            }
        }

        if (!shortestDistance.containsKey(destination)) {
            return new PathResult(false, Long.MAX_VALUE, Collections.<String>emptyList());
        }

        List<String> path = reconstructPath(previousNode, start, destination);
        return new PathResult(true, shortestDistance.get(destination), path);
    }

    private List<String> reconstructPath(Map<String, String> previous, String start, String destination) {
        // Follow previous nodes backwards, then reverse the result into start-to-end order.
        List<String> reversedPath = new ArrayList<>();
        String node = destination;
        reversedPath.add(node);
        while (!node.equals(start)) {
            node = previous.get(node);
            if (node == null) {
                return Collections.<String>emptyList();
            }
            reversedPath.add(node);
        }

        List<String> path = new ArrayList<>(reversedPath.size());
        for (int i = reversedPath.size() - 1; i >= 0; i--) {
            path.add(reversedPath.get(i));
        }
        return path;
    }

    private static class NodeDistance {
        private final String node;
        private final long distance;

        private NodeDistance(String node, long distance) {
            this.node = node;
            this.distance = distance;
        }

        private String getNode() {
            return node;
        }

        private long getDistance() {
            return distance;
        }
    }
}

```

## src/cpt204/graph/GraphAlgorithmComparisonResult.java

```java
package cpt204.graph;

public final class GraphAlgorithmComparisonResult {
    private final String algorithmName;
    private final GraphQueryResult queryResult;
    private final long runtimeNanos;

    public GraphAlgorithmComparisonResult(String algorithmName, GraphQueryResult queryResult, long runtimeNanos) {
        this.algorithmName = algorithmName;
        this.queryResult = queryResult;
        this.runtimeNanos = runtimeNanos;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public GraphQueryResult getQueryResult() {
        return queryResult;
    }

    public long getRuntimeNanos() {
        return runtimeNanos;
    }

    public double getRuntimeMillis() {
        return runtimeNanos / 1_000_000.0;
    }
}

```

## src/cpt204/graph/GraphAlgorithmComparisonService.java

```java
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

```

## src/cpt204/graph/GraphCaseDefinition.java

```java
package cpt204.graph;

import java.util.Collections;
import java.util.List;

public final class GraphCaseDefinition {
    private final String caseName;
    private final String start;
    private final String destination;
    private final List<String> waypointsInOrder;

    public GraphCaseDefinition(String caseName, String start, String destination, List<String> waypointsInOrder) {
        this.caseName = caseName;
        this.start = start;
        this.destination = destination;
        this.waypointsInOrder = waypointsInOrder;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getStart() {
        return start;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getWaypointsInOrder() {
        return Collections.unmodifiableList(waypointsInOrder);
    }
}

```

## src/cpt204/graph/GraphQueryResult.java

```java
package cpt204.graph;

import java.util.Collections;
import java.util.List;

public final class GraphQueryResult {
    private final String caseName;
    private final String start;
    private final String destination;
    private final List<String> waypointsInOrder;
    private final boolean reachable;
    private final long totalCost;
    private final List<String> finalPath;

    public GraphQueryResult(
            String caseName,
            String start,
            String destination,
            List<String> waypointsInOrder,
            boolean reachable,
            long totalCost,
            List<String> finalPath
    ) {
        this.caseName = caseName;
        this.start = start;
        this.destination = destination;
        this.waypointsInOrder = waypointsInOrder;
        this.reachable = reachable;
        this.totalCost = totalCost;
        this.finalPath = finalPath;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getStart() {
        return start;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getWaypointsInOrder() {
        return Collections.unmodifiableList(waypointsInOrder);
    }

    public boolean isReachable() {
        return reachable;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public List<String> getFinalPath() {
        return Collections.unmodifiableList(finalPath);
    }

    public String formatPath() {
        if (!reachable) {
            return "UNREACHABLE";
        }
        return String.join(" -> ", finalPath);
    }
}

```

## src/cpt204/graph/GraphQueryService.java

```java
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

        // Convert a waypoint query into several normal shortest-path queries.
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
                // Skip the first node of later segments to avoid duplicated waypoints.
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

```

## src/cpt204/graph/PathResult.java

```java
package cpt204.graph;

import java.util.Collections;
import java.util.List;

public final class PathResult {
    private final boolean reachable;
    private final long totalCost;
    private final List<String> path;

    public PathResult(boolean reachable, long totalCost, List<String> path) {
        this.reachable = reachable;
        this.totalCost = totalCost;
        this.path = path;
    }

    public boolean isReachable() {
        return reachable;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public List<String> getPath() {
        return Collections.unmodifiableList(path);
    }
}

```

## src/cpt204/graph/ShortestPathSolver.java

```java
package cpt204.graph;

public interface ShortestPathSolver {
    String getName();

    PathResult shortestPath(String start, String destination);
}

```

## src/cpt204/graph/WeightedGraph.java

```java
package cpt204.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeightedGraph {
    public static final class Edge {
        private final String to;
        private final int weight;

        public Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }

        public String getTo() {
            return to;
        }

        public int getWeight() {
            return weight;
        }
    }

    private final Map<String, List<Edge>> adjacency = new HashMap<>();
    private int undirectedEdgeCount = 0;

    public void addUndirectedEdge(String from, String to, int weight) {
        // The graph is undirected, so one CSV edge is stored in both directions.
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
        undirectedEdgeCount++;
    }

    private void addDirectedEdge(String from, String to, int weight) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, weight));
        // Make sure the destination node exists even if it has no outgoing edge yet.
        adjacency.computeIfAbsent(to, k -> new ArrayList<>());
    }

    public List<Edge> neighborsOf(String node) {
        if (!adjacency.containsKey(node)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(adjacency.get(node));
    }

    public Set<String> nodes() {
        return Collections.unmodifiableSet(adjacency.keySet());
    }

    public int getUndirectedEdgeCount() {
        return undirectedEdgeCount;
    }
}

```

## src/cpt204/io/CsvDataLoader.java

```java
package cpt204.io;

import cpt204.graph.WeightedGraph;
import cpt204.model.CandidateLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader {
    public List<CandidateLocation> readCandidates(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        List<CandidateLocation> candidates = new ArrayList<>();

        // Start from line 1 because line 0 is the CSV header.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            String locationId = parts[0].trim();
            int priorityScore = Integer.parseInt(parts[1].trim());
            candidates.add(new CandidateLocation(locationId, priorityScore));
        }
        return candidates;
    }

    public WeightedGraph readUndirectedWeightedGraph(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        WeightedGraph graph = new WeightedGraph();

        // Each row is one road between two locations with a distance.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            String from = parts[0].trim();
            String to = parts[1].trim();
            int weight = Integer.parseInt(parts[2].trim());
            graph.addUndirectedEdge(from, to, weight);
        }
        return graph;
    }
}

```

## src/cpt204/model/CandidateLocation.java

```java
package cpt204.model;

import java.util.Comparator;
import java.util.Objects;

public final class CandidateLocation {
    public static final Comparator<CandidateLocation> RANKING_RULE = new Comparator<CandidateLocation>() {
        @Override
        public int compare(CandidateLocation first, CandidateLocation second) {
            // Higher score comes first. If scores are equal, smaller location id comes first.
            if (first.priorityScore != second.priorityScore) {
                return second.priorityScore - first.priorityScore;
            }
            return first.locationId.compareTo(second.locationId);
        }
    };

    private final String locationId;
    private final int priorityScore;

    public CandidateLocation(String locationId, int priorityScore) {
        this.locationId = locationId;
        this.priorityScore = priorityScore;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    @Override
    public String toString() {
        return locationId + "(" + priorityScore + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CandidateLocation)) {
            return false;
        }
        CandidateLocation other = (CandidateLocation) obj;
        return priorityScore == other.priorityScore && Objects.equals(locationId, other.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, priorityScore);
    }
}

```

## src/cpt204/sort/BubbleSortStrategy.java

```java
package cpt204.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BubbleSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        int n = data.size();

        // Repeatedly move the largest remaining element to the back.
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (comparator.compare(data.get(j), data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                // If no swap happens, the list is already sorted.
                break;
            }
        }
    }
}

```

## src/cpt204/sort/DatasetCharacteristicsResult.java

```java
package cpt204.sort;

public final class DatasetCharacteristicsResult {
    private final String datasetName;
    private final String dataCharacteristic;
    private final int totalRows;
    private final long totalInversions;
    private final int bubblePasses;
    private final long bubbleSwaps;
    private final String pivotId;
    private final int pivotRank;
    private final int firstPartitionLeft;
    private final int firstPartitionRight;
    private final String pivotQuality;

    public DatasetCharacteristicsResult(
            String datasetName,
            String dataCharacteristic,
            int totalRows,
            long totalInversions,
            int bubblePasses,
            long bubbleSwaps,
            String pivotId,
            int pivotRank,
            int firstPartitionLeft,
            int firstPartitionRight,
            String pivotQuality
    ) {
        this.datasetName = datasetName;
        this.dataCharacteristic = dataCharacteristic;
        this.totalRows = totalRows;
        this.totalInversions = totalInversions;
        this.bubblePasses = bubblePasses;
        this.bubbleSwaps = bubbleSwaps;
        this.pivotId = pivotId;
        this.pivotRank = pivotRank;
        this.firstPartitionLeft = firstPartitionLeft;
        this.firstPartitionRight = firstPartitionRight;
        this.pivotQuality = pivotQuality;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getDataCharacteristic() {
        return dataCharacteristic;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public long getTotalInversions() {
        return totalInversions;
    }

    public int getBubblePasses() {
        return bubblePasses;
    }

    public long getBubbleSwaps() {
        return bubbleSwaps;
    }

    public String getPivotId() {
        return pivotId;
    }

    public int getPivotRank() {
        return pivotRank;
    }

    public int getFirstPartitionLeft() {
        return firstPartitionLeft;
    }

    public int getFirstPartitionRight() {
        return firstPartitionRight;
    }

    public String getPivotQuality() {
        return pivotQuality;
    }
}

```

## src/cpt204/sort/DatasetCharacteristicsService.java

```java
package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatasetCharacteristicsService {
    public DatasetCharacteristicsResult analyze(String datasetName, List<CandidateLocation> candidates) {
        long totalInversions = countTotalInversions(candidates);
        BubbleCheckResult bubbleCheckResult = simulateBubbleSort(candidates);
        PivotCheckResult pivotCheckResult = checkLastPivot(candidates);
        String dataCharacteristic = describeDataset(candidates, totalInversions);

        return new DatasetCharacteristicsResult(
                datasetName,
                dataCharacteristic,
                candidates.size(),
                totalInversions,
                bubbleCheckResult.getPasses(),
                bubbleCheckResult.getSwaps(),
                pivotCheckResult.getPivotId(),
                pivotCheckResult.getPivotRank(),
                pivotCheckResult.getLeftCount(),
                pivotCheckResult.getRightCount(),
                pivotCheckResult.getQuality()
        );
    }

    private long countTotalInversions(List<CandidateLocation> candidates) {
        long inversions = 0L;
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                if (CandidateLocation.RANKING_RULE.compare(candidates.get(i), candidates.get(j)) > 0) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    private BubbleCheckResult simulateBubbleSort(List<CandidateLocation> candidates) {
        List<CandidateLocation> data = new ArrayList<>(candidates);
        int passes = 0;
        long swaps = 0L;

        // This follows BubbleSortStrategy so the analysis matches the real code.
        for (int i = 0; i < data.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < data.size() - 1 - i; j++) {
                if (CandidateLocation.RANKING_RULE.compare(data.get(j), data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                    swaps++;
                    swapped = true;
                }
            }
            passes++;
            if (!swapped) {
                break;
            }
        }
        return new BubbleCheckResult(passes, swaps);
    }

    private PivotCheckResult checkLastPivot(List<CandidateLocation> candidates) {
        CandidateLocation pivot = candidates.get(candidates.size() - 1);
        int leftCount = 0;
        int rightCount = 0;

        // QuickSortStrategy uses the last element as pivot.
        for (int i = 0; i < candidates.size() - 1; i++) {
            int order = CandidateLocation.RANKING_RULE.compare(candidates.get(i), pivot);
            if (order <= 0) {
                leftCount++;
            } else {
                rightCount++;
            }
        }

        int pivotRank = leftCount + 1;
        String quality = describePivotQuality(leftCount, rightCount);
        return new PivotCheckResult(pivot.getLocationId(), pivotRank, leftCount, rightCount, quality);
    }

    private String describeDataset(List<CandidateLocation> candidates, long totalInversions) {
        int uniqueScores = DatasetProfile.from(candidates).getUniqueScoreCount();
        if (totalInversions <= 100) {
            return "Nearly sorted";
        }
        if (uniqueScores < candidates.size() / 2) {
            return "Scores sorted and IDs shuffled within tied-score groups";
        }
        return "Random order";
    }

    private String describePivotQuality(int leftCount, int rightCount) {
        int smallerSide = Math.min(leftCount, rightCount);
        int largerSide = Math.max(leftCount, rightCount);
        if (smallerSide == 0 || largerSide > smallerSide * 10) {
            return "Highly unbalanced";
        }
        if (largerSide > smallerSide * 3) {
            return "Unbalanced";
        }
        return "Relatively balanced";
    }

    private static class BubbleCheckResult {
        private final int passes;
        private final long swaps;

        private BubbleCheckResult(int passes, long swaps) {
            this.passes = passes;
            this.swaps = swaps;
        }

        private int getPasses() {
            return passes;
        }

        private long getSwaps() {
            return swaps;
        }
    }

    private static class PivotCheckResult {
        private final String pivotId;
        private final int pivotRank;
        private final int leftCount;
        private final int rightCount;
        private final String quality;

        private PivotCheckResult(String pivotId, int pivotRank, int leftCount, int rightCount, String quality) {
            this.pivotId = pivotId;
            this.pivotRank = pivotRank;
            this.leftCount = leftCount;
            this.rightCount = rightCount;
            this.quality = quality;
        }

        private String getPivotId() {
            return pivotId;
        }

        private int getPivotRank() {
            return pivotRank;
        }

        private int getLeftCount() {
            return leftCount;
        }

        private int getRightCount() {
            return rightCount;
        }

        private String getQuality() {
            return quality;
        }
    }
}

```

## src/cpt204/sort/DatasetProfile.java

```java
package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatasetProfile {
    private final int totalRows;
    private final int uniqueScoreCount;
    private final int tieScoreGroupCount;
    private final boolean alreadySortedByRankingRule;

    public DatasetProfile(int totalRows, int uniqueScoreCount, int tieScoreGroupCount, boolean alreadySortedByRankingRule) {
        this.totalRows = totalRows;
        this.uniqueScoreCount = uniqueScoreCount;
        this.tieScoreGroupCount = tieScoreGroupCount;
        this.alreadySortedByRankingRule = alreadySortedByRankingRule;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getUniqueScoreCount() {
        return uniqueScoreCount;
    }

    public int getTieScoreGroupCount() {
        return tieScoreGroupCount;
    }

    public boolean isAlreadySortedByRankingRule() {
        return alreadySortedByRankingRule;
    }

    public static DatasetProfile from(List<CandidateLocation> candidates) {
        Map<Integer, Integer> scoreCounts = new HashMap<>();

        // Count priority scores to see whether many locations have tied scores.
        for (CandidateLocation candidate : candidates) {
            scoreCounts.put(candidate.getPriorityScore(), scoreCounts.getOrDefault(candidate.getPriorityScore(), 0) + 1);
        }
        int tieGroups = 0;
        for (Integer count : scoreCounts.values()) {
            if (count > 1) {
                tieGroups++;
            }
        }

        boolean sorted = true;

        // Check whether the original file already follows the required ranking rule.
        for (int i = 0; i < candidates.size() - 1; i++) {
            CandidateLocation current = candidates.get(i);
            CandidateLocation next = candidates.get(i + 1);
            if (CandidateLocation.RANKING_RULE.compare(current, next) > 0) {
                sorted = false;
                break;
            }
        }
        return new DatasetProfile(candidates.size(), scoreCounts.size(), tieGroups, sorted);
    }
}

```

## src/cpt204/sort/DatasetSortingResult.java

```java
package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DatasetSortingResult {
    private final String datasetName;
    private final DatasetProfile profile;
    private final Map<String, Long> avgRuntimeByAlgorithmNanos;
    private final List<CandidateLocation> topSelected;

    public DatasetSortingResult(
            String datasetName,
            DatasetProfile profile,
            Map<String, Long> avgRuntimeByAlgorithmNanos,
            List<CandidateLocation> topSelected
    ) {
        this.datasetName = datasetName;
        this.profile = profile;
        this.avgRuntimeByAlgorithmNanos = avgRuntimeByAlgorithmNanos;
        this.topSelected = topSelected;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public DatasetProfile getProfile() {
        return profile;
    }

    public Map<String, Long> getAvgRuntimeByAlgorithmNanos() {
        return Collections.unmodifiableMap(avgRuntimeByAlgorithmNanos);
    }

    public List<CandidateLocation> getTopSelected() {
        return Collections.unmodifiableList(topSelected);
    }
}

```

## src/cpt204/sort/DataStructureComparisonResult.java

```java
package cpt204.sort;

public final class DataStructureComparisonResult {
    private final String datasetName;
    private final String listType;
    private final String algorithmName;
    private final int sampleSize;
    private final long avgRuntimeNanos;

    public DataStructureComparisonResult(
            String datasetName,
            String listType,
            String algorithmName,
            int sampleSize,
            long avgRuntimeNanos
    ) {
        this.datasetName = datasetName;
        this.listType = listType;
        this.algorithmName = algorithmName;
        this.sampleSize = sampleSize;
        this.avgRuntimeNanos = avgRuntimeNanos;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getListType() {
        return listType;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public long getAvgRuntimeNanos() {
        return avgRuntimeNanos;
    }

    public double getAvgRuntimeMillis() {
        return avgRuntimeNanos / 1_000_000.0;
    }
}

```

## src/cpt204/sort/DataStructureComparisonService.java

```java
package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DataStructureComparisonService {
    private static final int SAMPLE_SIZE = 200;
    private static final int RUNS = 3;

    public List<DataStructureComparisonResult> compareQuickSort(String datasetName, List<CandidateLocation> candidates) {
        List<CandidateLocation> sample = createSample(candidates);
        List<DataStructureComparisonResult> results = new ArrayList<>();
        QuickSortStrategy quickSort = new QuickSortStrategy();

        long arrayListTime = measureAverageTime(sample, "ArrayList", quickSort);
        long linkedListTime = measureAverageTime(sample, "LinkedList", quickSort);

        results.add(new DataStructureComparisonResult(datasetName, "ArrayList", quickSort.getName(), sample.size(), arrayListTime));
        results.add(new DataStructureComparisonResult(datasetName, "LinkedList", quickSort.getName(), sample.size(), linkedListTime));
        return results;
    }

    private List<CandidateLocation> createSample(List<CandidateLocation> candidates) {
        int limit = Math.min(SAMPLE_SIZE, candidates.size());
        return new ArrayList<>(candidates.subList(0, limit));
    }

    private long measureAverageTime(List<CandidateLocation> sample, String listType, QuickSortStrategy quickSort) {
        long totalTime = 0L;
        List<CandidateLocation> firstSortedResult = null;

        for (int run = 0; run < RUNS; run++) {
            List<CandidateLocation> data = createListByType(sample, listType);
            long start = System.nanoTime();
            quickSort.sort(data, CandidateLocation.RANKING_RULE);
            long end = System.nanoTime();
            totalTime += end - start;

            if (firstSortedResult == null) {
                firstSortedResult = new ArrayList<>(data);
            } else if (!firstSortedResult.equals(data)) {
                throw new IllegalStateException("Data structure comparison produced different sorted results.");
            }
        }
        return totalTime / RUNS;
    }

    private List<CandidateLocation> createListByType(List<CandidateLocation> sample, String listType) {
        if ("LinkedList".equals(listType)) {
            return new LinkedList<>(sample);
        }
        return new ArrayList<>(sample);
    }
}

```

## src/cpt204/sort/MergeSortStrategy.java

```java
package cpt204.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        if (data.size() <= 1) {
            return;
        }
        List<T> buffer = new ArrayList<>(data);
        mergeSort(data, buffer, comparator, 0, data.size() - 1);
    }

    private <T> void mergeSort(List<T> data, List<T> buffer, Comparator<T> comparator, int left, int right) {
        if (left >= right) {
            return;
        }

        // Split the list into two halves and sort them separately.
        int mid = left + (right - left) / 2;
        mergeSort(data, buffer, comparator, left, mid);
        mergeSort(data, buffer, comparator, mid + 1, right);
        merge(data, buffer, comparator, left, mid, right);
    }

    private <T> void merge(List<T> data, List<T> buffer, Comparator<T> comparator, int left, int mid, int right) {
        // Copy the current range so that merging will not overwrite unread values.
        for (int i = left; i <= right; i++) {
            buffer.set(i, data.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            // Always take the smaller ranked item from the two sorted halves.
            if (comparator.compare(buffer.get(i), buffer.get(j)) <= 0) {
                data.set(k++, buffer.get(i++));
            } else {
                data.set(k++, buffer.get(j++));
            }
        }

        while (i <= mid) {
            data.set(k++, buffer.get(i++));
        }
        while (j <= right) {
            data.set(k++, buffer.get(j++));
        }
    }
}

```

## src/cpt204/sort/QuickSortStrategy.java

```java
package cpt204.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickSortStrategy implements SortStrategy {
    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public <T> void sort(List<T> data, Comparator<T> comparator) {
        quickSort(data, comparator, 0, data.size() - 1);
    }

    private <T> void quickSort(List<T> data, Comparator<T> comparator, int low, int high) {
        if (low >= high) {
            return;
        }

        // Put the pivot in its final position, then sort the two sides.
        int pivotIndex = partition(data, comparator, low, high);
        quickSort(data, comparator, low, pivotIndex - 1);
        quickSort(data, comparator, pivotIndex + 1, high);
    }

    private <T> int partition(List<T> data, Comparator<T> comparator, int low, int high) {
        T pivot = data.get(high);
        int i = low - 1;

        // Values smaller than or equal to the pivot are moved to the left.
        for (int j = low; j < high; j++) {
            if (comparator.compare(data.get(j), pivot) <= 0) {
                i++;
                Collections.swap(data, i, j);
            }
        }
        Collections.swap(data, i + 1, high);
        return i + 1;
    }
}

```

## src/cpt204/sort/SortingService.java

```java
package cpt204.sort;

import cpt204.model.CandidateLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortingService {
    private final List<SortStrategy> strategies;
    private final int numberOfSelectedLocations;
    private final int benchmarkRuns;

    public SortingService(List<SortStrategy> strategies, int numberOfSelectedLocations, int benchmarkRuns) {
        this.strategies = strategies;
        this.numberOfSelectedLocations = numberOfSelectedLocations;
        this.benchmarkRuns = benchmarkRuns;
    }

    public DatasetSortingResult evaluateDataset(String datasetName, List<CandidateLocation> originalCandidates) {
        DatasetProfile profile = DatasetProfile.from(originalCandidates);
        Map<String, Long> averageTimeMap = new LinkedHashMap<>();
        List<CandidateLocation> firstSortedResult = null;

        for (SortStrategy strategy : strategies) {
            long totalTime = 0L;
            List<CandidateLocation> currentSortedResult = null;

            for (int run = 0; run < benchmarkRuns; run++) {
                // Each algorithm sorts a fresh copy, so the comparison is fair.
                List<CandidateLocation> candidatesForThisRun = new ArrayList<>(originalCandidates);
                long start = System.nanoTime();
                strategy.sort(candidatesForThisRun, CandidateLocation.RANKING_RULE);
                long end = System.nanoTime();
                totalTime += (end - start);
                currentSortedResult = candidatesForThisRun;
            }

            if (currentSortedResult == null) {
                throw new IllegalStateException("No sorted result produced for " + strategy.getName());
            }

            if (firstSortedResult == null) {
                firstSortedResult = currentSortedResult;
            } else if (!firstSortedResult.equals(currentSortedResult)) {
                throw new IllegalStateException(
                        "Sorting mismatch detected between algorithms in " + datasetName + "."
                );
            }

            long averageTime = totalTime / benchmarkRuns;
            averageTimeMap.put(strategy.getName(), averageTime);
        }

        if (firstSortedResult == null) {
            throw new IllegalStateException("No sorting result produced for " + datasetName);
        }

        List<CandidateLocation> selectedLocations = new ArrayList<>();
        int limit = Math.min(numberOfSelectedLocations, firstSortedResult.size());
        for (int i = 0; i < limit; i++) {
            selectedLocations.add(firstSortedResult.get(i));
        }

        return new DatasetSortingResult(datasetName, profile, averageTimeMap, selectedLocations);
    }
}

```

## src/cpt204/sort/SortStrategy.java

```java
package cpt204.sort;

import java.util.Comparator;
import java.util.List;

public interface SortStrategy {
    String getName();

    <T> void sort(List<T> data, Comparator<T> comparator);
}

```

## visualizers/cpt204/visualizer/GraphOutputVisualizer.java

```java
package cpt204.visualizer;

import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphQueryResult;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GraphOutputVisualizer {
    public void writePathSummary(Path outputFile, List<GraphQueryResult> graphResults) throws IOException {
        // Draw a report-ready image for the four required Task B cases.
        int width = 1500;
        int rowHeight = 150;
        int height = 110 + graphResults.size() * rowHeight;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Task B Shortest Path Cases", 40, 50);

        int y = 95;
        for (GraphQueryResult result : graphResults) {
            drawPathCase(g, result, 40, y, width - 80, rowHeight - 22);
            y += rowHeight;
        }

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeAlgorithmComparison(Path outputFile, List<GraphAlgorithmComparisonResult> results) throws IOException {
        // Draw a simple table and bar chart for the three shortest-path algorithms.
        int width = 1500;
        int height = 720;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Task B Shortest Path Algorithm Comparison", 40, 50);

        drawComparisonTable(g, results, 40, 90);
        drawRuntimeBars(g, results, 760, 95, 680, 520);

        g.dispose();
        saveImage(outputFile, image);
    }

    private void prepareCanvas(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
    }

    private void drawPathCase(Graphics2D g, GraphQueryResult result, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, width, height, 16, 16);

        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(result.getCaseName() + "   cost = " + result.getTotalCost(), x + 20, y + 32);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String subtitle = "start: " + result.getStart() + "    destination: " + result.getDestination()
                + "    waypoints: " + formatWaypoints(result);
        g.drawString(subtitle, x + 20, y + 58);

        List<String> path = result.getFinalPath();
        int nodeY = y + 95;
        int nodeX = x + 24;
        int maxX = x + width - 40;
        for (int i = 0; i < path.size(); i++) {
            String node = path.get(i);
            int boxWidth = 62;
            if (nodeX + boxWidth > maxX) {
                break;
            }
            drawNodeBox(g, node, nodeX, nodeY);
            if (i < path.size() - 1 && nodeX + 92 < maxX) {
                drawArrow(g, nodeX + 62, nodeY + 18, nodeX + 86, nodeY + 18);
            }
            nodeX += 92;
        }

        if (path.size() > 14) {
            g.setColor(new Color(110, 118, 130));
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Path is truncated in the image; full path is saved in graph_cases.csv.", maxX - 390, nodeY + 52);
        }
    }

    private String formatWaypoints(GraphQueryResult result) {
        if (result.getWaypointsInOrder().isEmpty()) {
            return "NONE";
        }
        return String.join(" -> ", result.getWaypointsInOrder());
    }

    private void drawNodeBox(Graphics2D g, String text, int x, int y) {
        g.setColor(new Color(221, 238, 255));
        g.fillRoundRect(x, y, 62, 36, 12, 12);
        g.setColor(new Color(52, 111, 186));
        g.drawRoundRect(x, y, 62, 36, 12, 12);

        g.setFont(new Font("Consolas", Font.BOLD, 14));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (62 - metrics.stringWidth(text)) / 2;
        g.setColor(new Color(20, 44, 76));
        g.drawString(text, textX, y + 23);
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(new Color(90, 100, 115));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x2 - 6, y2 - 5);
        g.drawLine(x2, y2, x2 - 6, y2 + 5);
    }

    private void drawComparisonTable(Graphics2D g, List<GraphAlgorithmComparisonResult> results, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, 670, 550, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, 670, 550, 16, 16);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(20, 36, 56));
        g.drawString("Algorithm", x + 18, y + 32);
        g.drawString("Case", x + 260, y + 32);
        g.drawString("Cost", x + 370, y + 32);
        g.drawString("Time (ms)", x + 470, y + 32);

        g.setFont(new Font("Arial", Font.PLAIN, 15));
        int rowY = y + 65;
        for (GraphAlgorithmComparisonResult result : results) {
            GraphQueryResult query = result.getQueryResult();
            g.setColor(new Color(20, 36, 56));
            g.drawString(result.getAlgorithmName(), x + 18, rowY);
            g.drawString(query.getCaseName(), x + 260, rowY);
            g.drawString(String.valueOf(query.getTotalCost()), x + 370, rowY);
            g.drawString(String.format("%.6f", result.getRuntimeMillis()), x + 470, rowY);
            rowY += 36;
        }
    }

    private void drawRuntimeBars(Graphics2D g, List<GraphAlgorithmComparisonResult> results, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, width, height, 16, 16);

        double maxTime = 1.0;
        for (GraphAlgorithmComparisonResult result : results) {
            maxTime = Math.max(maxTime, result.getRuntimeMillis());
        }

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(20, 36, 56));
        g.drawString("Runtime bars", x + 20, y + 30);

        int rowY = y + 62;
        for (GraphAlgorithmComparisonResult result : results) {
            int barWidth = (int) (420 * result.getRuntimeMillis() / maxTime);
            g.setColor(new Color(20, 36, 56));
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString(shortName(result.getAlgorithmName()) + " " + result.getQueryResult().getCaseName(), x + 20, rowY + 15);

            g.setColor(new Color(91, 141, 214));
            g.fillRoundRect(x + 170, rowY, Math.max(2, barWidth), 20, 8, 8);
            g.setColor(new Color(20, 36, 56));
            g.drawString(String.format("%.4f ms", result.getRuntimeMillis()), x + 600, rowY + 15);
            rowY += 38;
        }
    }

    private String shortName(String name) {
        if (name.startsWith("Bidirectional")) {
            return "Bi-Dijkstra";
        }
        return name;
    }

    private void saveImage(Path outputFile, BufferedImage image) throws IOException {
        Files.createDirectories(outputFile.getParent());
        ImageIO.write(image, "png", outputFile.toFile());
    }
}

```

## visualizers/cpt204/visualizer/SortingOutputVisualizer.java

```java
package cpt204.visualizer;

import cpt204.sort.DataStructureComparisonResult;
import cpt204.sort.DatasetCharacteristicsResult;
import cpt204.sort.DatasetSortingResult;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SortingOutputVisualizer {
    public void writeSortingRuntimeChart(Path outputFile, List<DatasetSortingResult> sortingResults) throws IOException {
        int width = 1300;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "Task A Sorting Runtime Comparison", 40, 50);
        drawRuntimeLines(g, sortingResults, 90, 120, 760, 450);
        drawRuntimeTable(g, sortingResults, 910, 120, 320, 360);

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeDatasetCharacteristicsChart(
            Path outputFile,
            List<DatasetCharacteristicsResult> results
    ) throws IOException {
        int width = 1300;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "Dataset Characteristics Used in Sorting Analysis", 40, 50);
        drawInversionBars(g, results, 70, 110, 540, 480);
        drawBubbleBars(g, results, 690, 110, 540, 480);

        g.dispose();
        saveImage(outputFile, image);
    }

    public void writeDataStructureChart(
            Path outputFile,
            List<DataStructureComparisonResult> results
    ) throws IOException {
        int width = 1300;
        int height = 720;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        prepareCanvas(g, width, height);

        drawTitle(g, "ArrayList vs LinkedList for Quick Sort", 40, 50);
        drawStructureBars(g, results, 80, 120, 1120, 460);

        g.setColor(new Color(90, 100, 115));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("This is a small supporting experiment on the first 200 rows of each dataset.", 90, 640);
        g.drawString("It explains why index-based sorting code is more suitable for ArrayList than LinkedList.", 90, 665);

        g.dispose();
        saveImage(outputFile, image);
    }

    private void prepareCanvas(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);
    }

    private void drawTitle(Graphics2D g, String title, int x, int y) {
        g.setColor(new Color(40, 76, 120));
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString(title, x, y);
    }

    private void drawRuntimeLines(Graphics2D g, List<DatasetSortingResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Average runtime (ms)");
        String[] algorithmNames = {"Bubble Sort", "Quick Sort", "Merge Sort"};
        Color[] colors = {
                new Color(40, 119, 190),
                new Color(219, 84, 47),
                new Color(23, 150, 115)
        };

        double maxTime = 1.0;
        for (DatasetSortingResult result : results) {
            for (Long nanos : result.getAvgRuntimeByAlgorithmNanos().values()) {
                maxTime = Math.max(maxTime, nanos / 1_000_000.0);
            }
        }

        int plotX = x + 80;
        int plotY = y + 70;
        int plotWidth = width - 140;
        int plotHeight = height - 140;
        drawAxis(g, plotX, plotY, plotWidth, plotHeight);

        for (int a = 0; a < algorithmNames.length; a++) {
            g.setColor(colors[a]);
            g.setStroke(new BasicStroke(3));
            int lastX = -1;
            int lastY = -1;
            for (int i = 0; i < results.size(); i++) {
                DatasetSortingResult result = results.get(i);
                long nanos = result.getAvgRuntimeByAlgorithmNanos().get(algorithmNames[a]);
                double millis = nanos / 1_000_000.0;
                int pointX = plotX + i * plotWidth / (results.size() - 1);
                int pointY = plotY + plotHeight - (int) (millis * plotHeight / maxTime);
                if (lastX >= 0) {
                    g.drawLine(lastX, lastY, pointX, pointY);
                }
                g.fillOval(pointX - 5, pointY - 5, 10, 10);
                lastX = pointX;
                lastY = pointY;
            }
            drawLegend(g, algorithmNames[a], colors[a], x + 90 + a * 180, y + height - 35);
        }

        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for (int i = 0; i < results.size(); i++) {
            int labelX = plotX + i * plotWidth / (results.size() - 1) - 28;
            g.drawString(results.get(i).getDatasetName(), labelX, plotY + plotHeight + 28);
        }
    }

    private void drawRuntimeTable(Graphics2D g, List<DatasetSortingResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Runtime table");
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(20, 36, 56));
        g.drawString("Dataset", x + 20, y + 58);
        g.drawString("Bubble", x + 110, y + 58);
        g.drawString("Quick", x + 190, y + 58);
        g.drawString("Merge", x + 260, y + 58);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int rowY = y + 95;
        for (DatasetSortingResult result : results) {
            Map<String, Long> runtimeMap = result.getAvgRuntimeByAlgorithmNanos();
            g.drawString(result.getDatasetName(), x + 20, rowY);
            g.drawString(formatMillis(runtimeMap.get("Bubble Sort")), x + 110, rowY);
            g.drawString(formatMillis(runtimeMap.get("Quick Sort")), x + 190, rowY);
            g.drawString(formatMillis(runtimeMap.get("Merge Sort")), x + 260, rowY);
            rowY += 38;
        }
    }

    private void drawInversionBars(Graphics2D g, List<DatasetCharacteristicsResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Total inversions");
        long maxValue = 1L;
        for (DatasetCharacteristicsResult result : results) {
            maxValue = Math.max(maxValue, result.getTotalInversions());
        }
        int rowY = y + 80;
        for (DatasetCharacteristicsResult result : results) {
            drawOneBar(g, result.getDatasetName(), result.getTotalInversions(), maxValue, x + 35, rowY, width - 130, new Color(40, 119, 190));
            rowY += 90;
        }
    }

    private void drawBubbleBars(Graphics2D g, List<DatasetCharacteristicsResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Bubble sort passes");
        long maxValue = 1L;
        for (DatasetCharacteristicsResult result : results) {
            maxValue = Math.max(maxValue, result.getBubblePasses());
        }
        int rowY = y + 80;
        for (DatasetCharacteristicsResult result : results) {
            drawOneBar(g, result.getDatasetName(), result.getBubblePasses(), maxValue, x + 35, rowY, width - 130, new Color(23, 150, 115));
            rowY += 90;
        }
    }

    private void drawStructureBars(Graphics2D g, List<DataStructureComparisonResult> results, int x, int y, int width, int height) {
        drawChartBox(g, x, y, width, height, "Quick Sort runtime by list type");
        double maxValue = 1.0;
        for (DataStructureComparisonResult result : results) {
            maxValue = Math.max(maxValue, result.getAvgRuntimeMillis());
        }

        int rowY = y + 75;
        for (DataStructureComparisonResult result : results) {
            String label = result.getDatasetName() + " " + result.getListType();
            Color color = "ArrayList".equals(result.getListType()) ? new Color(40, 119, 190) : new Color(219, 84, 47);
            drawOneBar(g, label, result.getAvgRuntimeMillis(), maxValue, x + 35, rowY, width - 140, color);
            rowY += 58;
        }
    }

    private void drawChartBox(Graphics2D g, int x, int y, int width, int height, String title) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(210, 218, 230));
        g.drawRoundRect(x, y, width, height, 16, 16);
        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(title, x + 20, y + 32);
    }

    private void drawAxis(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(170, 180, 195));
        g.setStroke(new BasicStroke(2));
        g.drawLine(x, y + height, x + width, y + height);
        g.drawLine(x, y, x, y + height);
    }

    private void drawLegend(Graphics2D g, String text, Color color, int x, int y) {
        g.setColor(color);
        g.fillRoundRect(x, y - 12, 22, 12, 5, 5);
        g.setColor(new Color(20, 36, 56));
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(text, x + 30, y);
    }

    private void drawOneBar(Graphics2D g, String label, long value, long maxValue, int x, int y, int width, Color color) {
        drawOneBar(g, label, (double) value, (double) maxValue, x, y, width, color);
    }

    private void drawOneBar(Graphics2D g, String label, double value, double maxValue, int x, int y, int width, Color color) {
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(new Color(20, 36, 56));
        g.drawString(label, x, y + 16);

        int barX = x + 150;
        int barWidth = (int) (width * value / maxValue);
        g.setColor(new Color(232, 236, 242));
        g.fillRoundRect(barX, y, width, 22, 8, 8);
        g.setColor(color);
        g.fillRoundRect(barX, y, Math.max(2, barWidth), 22, 8, 8);
        g.setColor(new Color(20, 36, 56));
        int valueX = barX + Math.min(barWidth + 10, width - 70);
        g.drawString(formatNumber(value), valueX, y + 16);
    }

    private String formatMillis(Long nanos) {
        return String.format("%.3f", nanos / 1_000_000.0);
    }

    private String formatNumber(double value) {
        if (value >= 1000) {
            return String.format("%.0f", value);
        }
        return String.format("%.3f", value);
    }

    private void saveImage(Path outputFile, BufferedImage image) throws IOException {
        Files.createDirectories(outputFile.getParent());
        ImageIO.write(image, "png", outputFile.toFile());
    }
}

```
