package searchengine.strategies.ranking;

import java.util.Comparator;
import java.util.List;
import searchengine.entities.SearchResult;

public class ScoreBasedRankingStrategy implements RankingStrategy {
  @Override
  public void rank(List<SearchResult> results) {
    results.sort(Comparator.comparingDouble(SearchResult::getScore).reversed());
  }
}
