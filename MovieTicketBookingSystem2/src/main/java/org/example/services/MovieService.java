package org.example.services;

import java.util.HashMap;
import java.util.Map;
import org.example.enums.MovieGenre;
import org.example.models.Movie;

public class MovieService {
  private final Map<Integer, Movie> movies;
  private int movieCounter; // Private counter for generating movie IDs

  public MovieService() {
    this.movies = new HashMap<>();
    this.movieCounter = 0; // Initialize the counter to 0
  }

  public Movie getMovie(final int movieId) throws Exception {
    if (!movies.containsKey(movieId)) {
      throw new Exception("Movie with ID " + movieId + " not found.");
    }
    return movies.get(movieId);
  }

  public Movie createMovie(
      final String movieName, final int durationInMinutes, final MovieGenre movieGenre) {
    int movieId = ++movieCounter;
    Movie movie = new Movie(movieId, movieName, durationInMinutes, movieGenre);
    movies.put(movieId, movie);
    return movie;
  }
}
