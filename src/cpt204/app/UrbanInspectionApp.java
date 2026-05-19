package cpt204.app;

import cpt204.graph.AStarSolver;
import cpt204.graph.BidirectionalDijkstraSolver;
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
import cpt204.sort.SortingOperationCountResult;
import cpt204.sort.SortingOperationCountService;
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

        ExperimentOutputWriter outputWriter = new ExperimentOutputWriter();
        Path outputDir = createOutputDirForThisRun();
        outputWriter.writeAll(
                outputDir,
                sortingResults,
                selectedByDataset,
                graphResults,
                graphComparisonResults,
                datasetCharacteristicsResults,
                dataStructureComparisonResults,
                sortingOperationCountResults,
                graphOperationCountResults
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
                    "%s | %s | rows=%d | %s avg=%d ns (%.6f ms)%n",
                    result.getDatasetName(),
                    result.getListType(),
                    result.getRowCount(),
                    result.getAlgorithmName(),
                    result.getAvgRuntimeNanos(),
                    result.getAvgRuntimeMillis()
            );
        }
        System.out.println();
    }

    private static void printSortingOperationCounts(List<SortingOperationCountResult> results) {
        System.out.println("========== Task A: Sorting Operation Counts ==========");
        for (SortingOperationCountResult result : results) {
            System.out.printf(
                    "%s | %s | comparisons=%d | swaps=%d | writes=%d | passes=%d | partitions=%d | merges=%d%n",
                    result.getDatasetName(),
                    result.getAlgorithmName(),
                    result.getComparisons(),
                    result.getSwaps(),
                    result.getWrites(),
                    result.getPasses(),
                    result.getPartitions(),
                    result.getMerges()
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

    private static void printGraphOperationCounts(List<GraphOperationCountResult> results) {
        System.out.println("========== Task B: Graph Operation Counts ==========");
        for (GraphOperationCountResult result : results) {
            System.out.printf(
                    "%s | %s | edgeChecks=%d | relaxations=%d | queuePolls=%d | visitedNodes=%d%n",
                    result.getAlgorithmName(),
                    result.getCaseName(),
                    result.getEdgeChecks(),
                    result.getSuccessfulRelaxations(),
                    result.getQueuePolls(),
                    result.getVisitedNodes()
            );
        }
        System.out.println();
    }
}
