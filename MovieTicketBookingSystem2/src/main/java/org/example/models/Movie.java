package org.example.models;

import org.example.enums.MovieGenre;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Movie {
  private final int movieId;
  private final String movieName;
  private final int movieDurationInMinutes;
  private final MovieGenre movieGenre;
}
