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
