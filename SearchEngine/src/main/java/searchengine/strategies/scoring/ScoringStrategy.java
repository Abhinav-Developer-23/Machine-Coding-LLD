package searchengine.strategies.scoring;

import searchengine.entities.Document;
import searchengine.entities.Posting;

public interface ScoringStrategy {
  double calculateScore(String term, Posting posting, Document document);
}
