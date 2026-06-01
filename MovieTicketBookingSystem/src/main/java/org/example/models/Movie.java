package org.example.models;

import lombok.Getter;

@Getter
public class Movie extends MovieSubject {
  private final String id;
  private final String title;
  private final int durationInMinutes;

  public Movie(String id, String title, int durationInMinutes) {
    this.id = id;
    this.title = title;
    this.durationInMinutes = durationInMinutes;
  }

  // Additional movie details like genre, language etc. can be added here
}
