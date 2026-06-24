package searchengine.strategies.scoring;

import searchengine.entities.Document;
import searchengine.entities.Posting;

public class TermFrequencyScoringStrategy implements ScoringStrategy {
  @Override
  public double calculateScore(String term, Posting posting, Document document) {
    return posting.getFrequency();
  }
}
