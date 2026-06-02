package org.example.controllers;

import org.example.enums.MovieGenre;
import org.example.services.MovieService;

public class MovieController {
  // Reference to the MovieService which contains the business logic related to movies
  private final MovieService movieService;

  // Constructor to initialize the MovieService dependency
  public MovieController(final MovieService movieService) {
    this.movieService = movieService;
  }

  public int createMovie(
      final String movieName, final int durationInMinutes, final MovieGenre movieGenre) {
    return movieService.createMovie(movieName, durationInMinutes, movieGenre).getMovieId();
  }
}
