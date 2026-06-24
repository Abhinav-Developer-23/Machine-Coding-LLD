package strategies;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import entities.Song;

public class SimpleRecommendationStrategy implements RecommendationStrategy {
  @Override
  public List<Song> recommend(List<Song> allSongs) {
    System.out.println("Generating recommendations...");
    return allSongs.stream()
        .sorted(Comparator.comparing(Song::getTitle))
        .limit(5)
        .collect(Collectors.toList());
  }
}
