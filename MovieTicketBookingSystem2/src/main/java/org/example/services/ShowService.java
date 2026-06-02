package org.example.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.models.Movie;
import org.example.models.Screen;
import org.example.models.Show;

public class ShowService {
  private final Map<Integer, Show> shows; // Map to hold all created shows (key = show ID)
  private int showCounter; // Counter to generate unique IDs for each show

  // Constructor initializing the shows map and show counter
  public ShowService() {
    this.shows = new HashMap<>();
    this.showCounter = 0;
  }

  // Retrieves a show by ID, throws exception if not found
  public Show getShow(final int showId) throws Exception {
    if (!shows.containsKey(showId)) {
      throw new Exception("Show with ID " + showId + " not found.");
    }
    return shows.get(showId);
  }

  public Show createShow(
      final Movie movie,
      final Screen screen,
      final Date startTime,
      final Integer durationInSeconds) {
    // Generate a unique show ID
    int showId = ++showCounter;
    // Create and store the new show
    final Show show = new Show(showId, movie, screen, startTime, durationInSeconds);
    this.shows.put(showId, show);
    return show;
  }

  private List<Show> getShowsForScreen(final Screen screen) {
    final List<Show> response = new ArrayList<>();
    for (Show show : shows.values()) {
      if (show.getScreen().getScreenId() == screen.getScreenId()) { // Compare by screen ID
        response.add(show);
      }
    }
    return response;
  }
}
