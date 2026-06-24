package strategies;

import java.util.List;

import entities.Song;

public interface RecommendationStrategy {
  List<Song> recommend(List<Song> allSongs);
}
