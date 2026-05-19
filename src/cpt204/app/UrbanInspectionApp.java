package cpt204.app;

import cpt204.graph.BfsSolver;
import cpt204.graph.DijkstraSolver;
import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphAlgorithmComparisonService;
import cpt204.graph.GraphCaseDefinition;
import cpt204.graph.GraphOperationCountResult;
import cpt204.graph.GraphOperationCountService;
import cpt204.graph.GraphQueryResult;
import cpt204.graph.GraphQueryService;
import cpt204.graph.ShortestPathSolver;
import cpt204.graph.WeightedGraph;
import cpt204.io.BorderedTable;
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
import cpt204.sort.SortingOperationCountResult;
import cpt204.sort.SortingOperationCountService;
import cpt204.sort.SortingService;
import cpt204.chart.GraphOutputVisualizer;
import cpt204.chart.SortingOutputVisualizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UrbanInspectionApp {
    private static final Path DATA_DIR = Paths.get("Group Project Datasets");
    private static final Path OUTPUT_DIR = Paths.get("outputs");
    private static final int TOP_N = 10;
    private static final int BENCHMARK_RUNS = 5;

    public static void main(String[] args) throws IOException {
        CsvDataLoader loader = new CsvDataLoader();

        Map<String, List<CandidateLocation>> datasets = new LinkedHashMap<>();
        datasets.put("Dataset A", loader.readCandidates(DATA_DIR.resolve("candidates_A.csv")));
        datasets.put("Dataset B", loader.readCandidates(DATA_DIR.resolve("candidates_B.csv")));
        datasets.put("Dataset C", loader.readCandidates(DATA_DIR.resolve("candidates_C.csv")));

        List<SortStrategy> strategies = Arrays.asList(
                new BubbleSortStrategy(),
                new QuickSortStrategy(),
                new MergeSortStrategy()
        );
        SortingService sortingService = new SortingService(strategies, TOP_N, BENCHMARK_RUNS);

        List<DatasetSortingResult> sortingResults = new ArrayList<>();
        List<DatasetCharacteristicsResult> datasetCharacteristicsResults = new ArrayList<>();
        List<DataStructureComparisonResult> dataStructureComparisonResults = new ArrayList<>();
        List<SortingOperationCountResult> sortingOperationCountResults = new ArrayList<>();
        Map<String, List<CandidateLocation>> selectedByDataset = new LinkedHashMap<>();
        DatasetCharacteristicsService characteristicsService = new DatasetCharacteristicsService();
        DataStructureComparisonService dataStructureService = new DataStructureComparisonService();
        SortingOperationCountService sortingOperationService = new SortingOperationCountService();

        for (Map.Entry<String, List<CandidateLocation>> entry : datasets.entrySet()) {
            DatasetSortingResult result = sortingService.evaluateDataset(entry.getKey(), entry.getValue());
            sortingResults.add(result);
            datasetCharacteristicsResults.add(characteristicsService.analyze(entry.getKey(), entry.getValue()));
            dataStructureComparisonResults.addAll(dataStructureService.compareQuickSort(entry.getKey(), entry.getValue()));
            sortingOperationCountResults.addAll(sortingOperationService.countOperations(entry.getKey(), entry.getValue()));
            selectedByDataset.put(entry.getKey(), result.getTopSelected());
        }

        WeightedGraph graph = loader.readUndirectedWeightedGraph(DATA_DIR.resolve("paths.csv"));
        DijkstraSolver dijkstraSolver = new DijkstraSolver(graph);
        GraphQueryService graphQueryService = new GraphQueryService(dijkstraSolver);

        List<CandidateLocation> topA = selectedByDataset.get("Dataset A");
        List<CandidateLocation> topB = selectedByDataset.get("Dataset B");
        List<CandidateLocation> topC = selectedByDataset.get("Dataset C");

        String a1 = topA.get(0).getLocationId();
        String a10 = topA.get(9).getLocationId();
        String b1 = topB.get(0).getLocationId();
        String b5 = topB.get(4).getLocationId();
        String c1 = topC.get(0).getLocationId();
        String c5 = topC.get(4).getLocationId();

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
                new BfsSolver(graph)
        );
        GraphAlgorithmComparisonService comparisonService = new GraphAlgorithmComparisonService();
        List<GraphAlgorithmComparisonResult> graphComparisonResults =
                comparisonService.compareAlgorithms(graphSolvers, graphCaseDefinitions);
        GraphOperationCountService graphOperationService = new GraphOperationCountService(graph);
        List<GraphOperationCountResult> graphOperationCountResults =
                graphOperationService.countOperations(graphCaseDefinitions);

        printSortingSummary(sortingResults);
        printDatasetCharacteristics(datasetCharacteristicsResults);
        printSortingOperationCounts(sortingOperationCountResults);
        printDataStructureComparison(dataStructureComparisonResults);
        printSelectedLocations(selectedByDataset);
        printGraphSummary(graph, graphResults);
        printGraphAlgorithmComparison(graphComparisonResults);
        printGraphOperationCounts(graphOperationCountResults);

        Files.createDirectories(OUTPUT_DIR);
        ExperimentOutputWriter outputWriter = new ExperimentOutputWriter();
        outputWriter.writeAll(
                OUTPUT_DIR,
                sortingResults,
                selectedByDataset,
                graphResults,
                graphComparisonResults,
                datasetCharacteristicsResults,
                dataStructureComparisonResults,
                sortingOperationCountResults,
                graphOperationCountResults
        );

        GraphOutputVisualizer graphVisualizer = new GraphOutputVisualizer();
        graphVisualizer.writePathSummary(OUTPUT_DIR.resolve("graph_path_visualization.png"), graphResults);
        graphVisualizer.writeAlgorithmComparison(OUTPUT_DIR.resolve("graph_algorithm_comparison.png"), graphComparisonResults);

        SortingOutputVisualizer sortingVisualizer = new SortingOutputVisualizer();
        sortingVisualizer.writeSortingRuntimeChart(OUTPUT_DIR.resolve("sorting_runtime_chart.png"), sortingResults);
        sortingVisualizer.writeDatasetCharacteristicsChart(
                OUTPUT_DIR.resolve("dataset_characteristics_chart.png"),
                datasetCharacteristicsResults
        );
        sortingVisualizer.writeDataStructureChart(
                OUTPUT_DIR.resolve("data_structure_comparison_chart.png"),
                dataStructureComparisonResults
        );

        System.out.println();
        System.out.println("Written to " + OUTPUT_DIR.toAbsolutePath());
    }

    private static void printSortingSummary(List<DatasetSortingResult> sortingResults) {
        System.out.println("Task A: Sorting Benchmark");
        for (DatasetSortingResult result : sortingResults) {
            System.out.println(result.getDatasetName());
            BorderedTable profile = new BorderedTable("totalRows", "uniqueScores", "tieGroups", "sortedByRule");
            profile.addRow(
                    String.valueOf(result.getProfile().getTotalRows()),
                    String.valueOf(result.getProfile().getUniqueScoreCount()),
                    String.valueOf(result.getProfile().getTieScoreGroupCount()),
                    String.valueOf(result.getProfile().isAlreadySortedByRankingRule())
            );
            profile.printToConsole();

            BorderedTable timings = new BorderedTable("Algorithm", "Avg_ns", "Avg_ms");
            for (Map.Entry<String, Long> runtimeEntry : result.getAvgRuntimeByAlgorithmNanos().entrySet()) {
                long nanos = runtimeEntry.getValue();
                timings.addRow(
                        runtimeEntry.getKey(),
                        String.valueOf(nanos),
                        String.format("%.6f", nanos / 1_000_000.0)
                );
            }
            timings.printToConsole();
            System.out.println();
        }
    }

    private static void printDatasetCharacteristics(List<DatasetCharacteristicsResult> results) {
        System.out.println("Task A: Dataset Characteristics");
        BorderedTable table = new BorderedTable(
                "Dataset", "Characteristic", "Inversions", "BubblePasses", "BubbleSwaps",
                "PivotId", "PartitionLeft", "PartitionRight", "PivotQuality"
        );
        for (DatasetCharacteristicsResult result : results) {
            table.addRow(
                    result.getDatasetName(),
                    result.getDataCharacteristic(),
                    String.valueOf(result.getTotalInversions()),
                    String.valueOf(result.getBubblePasses()),
                    String.valueOf(result.getBubbleSwaps()),
                    result.getPivotId(),
                    String.valueOf(result.getFirstPartitionLeft()),
                    String.valueOf(result.getFirstPartitionRight()),
                    result.getPivotQuality()
            );
        }
        table.printToConsole();
        System.out.println();
    }

    private static void printDataStructureComparison(List<DataStructureComparisonResult> results) {
        System.out.println("Task C: Data Structure Comparison");
        BorderedTable table = new BorderedTable("Dataset", "ListType", "Rows", "Algorithm", "Avg_ns", "Avg_ms");
        for (DataStructureComparisonResult result : results) {
            table.addRow(
                    result.getDatasetName(),
                    result.getListType(),
                    String.valueOf(result.getRowCount()),
                    result.getAlgorithmName(),
                    String.valueOf(result.getAvgRuntimeNanos()),
                    String.format("%.6f", result.getAvgRuntimeMillis())
            );
        }
        table.printToConsole();
        System.out.println();
    }

    private static void printSortingOperationCounts(List<SortingOperationCountResult> results) {
        System.out.println("Task A: Sorting Operation Counts");
        BorderedTable table = new BorderedTable(
                "Dataset", "Algorithm", "Comparisons", "Swaps", "Writes", "Passes", "Partitions", "Merges"
        );
        for (SortingOperationCountResult result : results) {
            table.addRow(
                    result.getDatasetName(),
                    result.getAlgorithmName(),
                    String.valueOf(result.getComparisons()),
                    String.valueOf(result.getSwaps()),
                    String.valueOf(result.getWrites()),
                    String.valueOf(result.getPasses()),
                    String.valueOf(result.getPartitions()),
                    String.valueOf(result.getMerges())
            );
        }
        table.printToConsole();
        System.out.println();
    }

    private static void printSelectedLocations(Map<String, List<CandidateLocation>> selectedByDataset) {
        System.out.println("Top 10 Selected Locations");
        for (Map.Entry<String, List<CandidateLocation>> entry : selectedByDataset.entrySet()) {
            System.out.println(entry.getKey());
            BorderedTable table = new BorderedTable("Rank", "Location", "Score");
            List<CandidateLocation> selected = entry.getValue();
            for (int i = 0; i < selected.size(); i++) {
                CandidateLocation location = selected.get(i);
                table.addRow(
                        String.format("%02d", i + 1),
                        location.getLocationId(),
                        String.valueOf(location.getPriorityScore())
                );
            }
            table.printToConsole();
            System.out.println();
        }
    }

    private static void printGraphSummary(WeightedGraph graph, List<GraphQueryResult> graphResults) {
        System.out.println("Task B: Graph Queries");
        BorderedTable meta = new BorderedTable("Nodes", "UndirectedEdges");
        meta.addRow(String.valueOf(graph.nodes().size()), String.valueOf(graph.getUndirectedEdgeCount()));
        meta.printToConsole();
        System.out.println();

        for (GraphQueryResult result : graphResults) {
            System.out.println(result.getCaseName());
            String waypoints = result.getWaypointsInOrder().isEmpty()
                    ? "NONE"
                    : String.join(" -> ", result.getWaypointsInOrder());
            String path = result.isReachable() ? result.formatPath() : "UNREACHABLE";
            String cost = result.isReachable() ? String.valueOf(result.getTotalCost()) : "INF";
            BorderedTable table = new BorderedTable("Start", "Destination", "Waypoints", "Cost", "Path");
            table.addRow(result.getStart(), result.getDestination(), waypoints, cost, path);
            table.printToConsole();
            System.out.println();
        }
    }

    private static void printGraphAlgorithmComparison(List<GraphAlgorithmComparisonResult> comparisonResults) {
        System.out.println("Task B: Algorithm Comparison");
        BorderedTable table = new BorderedTable("Case", "Algorithm", "Reachable", "Cost", "Hops", "Time_ms", "Path");
        for (GraphAlgorithmComparisonResult comparisonResult : comparisonResults) {
            GraphQueryResult queryResult = comparisonResult.getQueryResult();
            String cost = queryResult.isReachable() ? String.valueOf(queryResult.getTotalCost()) : "INF";
            int hops = queryResult.isReachable()
                    ? Math.max(0, queryResult.getFinalPath().size() - 1)
                    : -1;
            table.addRow(
                    queryResult.getCaseName(),
                    comparisonResult.getAlgorithmName(),
                    String.valueOf(queryResult.isReachable()),
                    cost,
                    String.valueOf(hops),
                    String.format("%.6f", comparisonResult.getRuntimeMillis()),
                    queryResult.formatPath()
            );
        }
        table.printToConsole();
        System.out.println();
    }

    private static void printGraphOperationCounts(List<GraphOperationCountResult> results) {
        System.out.println("Task B: Graph Operation Counts");
        BorderedTable table = new BorderedTable(
                "Case", "Algorithm", "EdgeChecks", "Relaxations", "QueuePolls", "VisitedNodes"
        );
        for (GraphOperationCountResult result : results) {
            table.addRow(
                    result.getCaseName(),
                    result.getAlgorithmName(),
                    String.valueOf(result.getEdgeChecks()),
                    String.valueOf(result.getSuccessfulRelaxations()),
                    String.valueOf(result.getQueuePolls()),
                    String.valueOf(result.getVisitedNodes())
            );
        }
        table.printToConsole();
        System.out.println();
    }
}
