package searchengine.strategies.ranking;

import java.util.List;
import searchengine.entities.SearchResult;

public interface RankingStrategy {
  void rank(List<SearchResult> results);
}
