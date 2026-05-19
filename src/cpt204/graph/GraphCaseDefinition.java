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
