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
