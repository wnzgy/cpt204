package cpt204.app;

import cpt204.graph.GraphAlgorithmComparisonResult;
import cpt204.graph.GraphOperationCountResult;
import cpt204.graph.GraphQueryResult;
import cpt204.model.CandidateLocation;
import cpt204.sort.DataStructureComparisonResult;
import cpt204.sort.DatasetCharacteristicsResult;
import cpt204.sort.DatasetProfile;
import cpt204.sort.DatasetSortingResult;
import cpt204.sort.SortingOperationCountResult;

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
            List<DataStructureComparisonResult> dataStructureComparisonResults,
            List<SortingOperationCountResult> sortingOperationCountResults,
            List<GraphOperationCountResult> graphOperationCountResults
    ) throws IOException {
        Files.createDirectories(outputDir);
        writeSortingBenchmark(outputDir.resolve("sorting_benchmark.csv"), sortingResults);
        writeDatasetProfiles(outputDir.resolve("dataset_profiles.csv"), sortingResults);
        writeDatasetCharacteristics(outputDir.resolve("dataset_characteristics_core.csv"), datasetCharacteristicsResults);
        writeDataStructureComparison(outputDir.resolve("data_structure_comparison.csv"), dataStructureComparisonResults);
        writeSortingOperationCounts(outputDir.resolve("sorting_operation_counts.csv"), sortingOperationCountResults);
        writeSelectedLocations(outputDir.resolve("selected_locations.csv"), selectedByDataset);
        writeGraphCases(outputDir.resolve("graph_cases.csv"), graphResults);
        writeGraphAlgorithmComparison(outputDir.resolve("graph_algorithm_comparison.csv"), graphComparisonResults);
        writeGraphOperationCounts(outputDir.resolve("graph_operation_counts.csv"), graphOperationCountResults);
    }

    private void writeSortingBenchmark(Path file, List<DatasetSortingResult> sortingResults) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,algorithm,avg_runtime_ns,avg_runtime_ms\n");

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
        builder.append("dataset,list_type,algorithm,row_count,avg_runtime_ns,avg_runtime_ms\n");

        for (DataStructureComparisonResult result : results) {
            builder.append(result.getDatasetName()).append(",")
                    .append(result.getListType()).append(",")
                    .append(result.getAlgorithmName()).append(",")
                    .append(result.getRowCount()).append(",")
                    .append(result.getAvgRuntimeNanos()).append(",")
                    .append(String.format("%.6f", result.getAvgRuntimeMillis()))
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeSortingOperationCounts(
            Path file,
            List<SortingOperationCountResult> results
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,algorithm,row_count,comparisons,swaps,writes,passes,partitions,merges,recursive_calls\n");

        for (SortingOperationCountResult result : results) {
            builder.append(result.getDatasetName()).append(",")
                    .append(result.getAlgorithmName()).append(",")
                    .append(result.getRowCount()).append(",")
                    .append(result.getComparisons()).append(",")
                    .append(result.getSwaps()).append(",")
                    .append(result.getWrites()).append(",")
                    .append(result.getPasses()).append(",")
                    .append(result.getPartitions()).append(",")
                    .append(result.getMerges()).append(",")
                    .append(result.getRecursiveCalls())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeSelectedLocations(Path file, Map<String, List<CandidateLocation>> selectedByDataset) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("dataset,rank,location_id,priority_score\n");

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

    private void writeGraphOperationCounts(
            Path file,
            List<GraphOperationCountResult> results
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("algorithm,case,segment_count,reachable,total_cost,queue_polls,stale_polls,queue_pushes,edge_checks,successful_relaxations,visited_nodes\n");

        for (GraphOperationCountResult result : results) {
            String cost = result.isReachable() ? String.valueOf(result.getTotalCost()) : "INF";
            builder.append(result.getAlgorithmName()).append(",")
                    .append(result.getCaseName()).append(",")
                    .append(result.getSegmentCount()).append(",")
                    .append(result.isReachable()).append(",")
                    .append(cost).append(",")
                    .append(result.getQueuePolls()).append(",")
                    .append(result.getStalePolls()).append(",")
                    .append(result.getQueuePushes()).append(",")
                    .append(result.getEdgeChecks()).append(",")
                    .append(result.getSuccessfulRelaxations()).append(",")
                    .append(result.getVisitedNodes())
                    .append("\n");
        }
        Files.write(file, builder.toString().getBytes(StandardCharsets.UTF_8));
    }
}
