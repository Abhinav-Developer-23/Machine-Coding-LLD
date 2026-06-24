package searchengine.strategies.scoring;

import searchengine.entities.Document;
import searchengine.entities.Posting;

public class TitleBoostScoringStrategy implements ScoringStrategy {
  private static final double TITLE_BOOST_FACTOR = 1.5;

  @Override
  public double calculateScore(String term, Posting posting, Document document) {
    double score = posting.getFrequency();
    if (document.getTitle().toLowerCase().contains(term)) {
      score *= TITLE_BOOST_FACTOR;
    }
    return score;
  }
}
