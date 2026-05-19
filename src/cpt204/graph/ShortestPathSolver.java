package cpt204.graph;

public interface ShortestPathSolver {
    String getName();

    PathResult shortestPath(String start, String destination);
}
