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
