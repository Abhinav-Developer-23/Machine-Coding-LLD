package services;

import java.util.List;

import entities.Song;
import lombok.Setter;
import strategies.RecommendationStrategy;

public class RecommendationService {
  @Setter private RecommendationStrategy strategy;

  public RecommendationService(RecommendationStrategy strategy) {
    this.strategy = strategy;
  }

  public List<Song> generateRecommendations(List<Song> allSongs) {
    return strategy.recommend(allSongs);
  }
}
