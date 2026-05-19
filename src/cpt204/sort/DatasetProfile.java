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
