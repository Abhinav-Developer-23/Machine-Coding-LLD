package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.enums.MovieGenre;

@Getter
@AllArgsConstructor
public class Movie {
  private final int movieId;
  private final String movieName;
  private final int movieDurationInMinutes;
  private final MovieGenre movieGenre;
}
