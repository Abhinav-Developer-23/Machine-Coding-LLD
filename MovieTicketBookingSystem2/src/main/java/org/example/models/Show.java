package org.example.models;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Show {
  private final int id; // Unique identifier for the show
  private final Movie movie; // The movie being shown
  private final Screen screen; // The screen where the show is played
  private final Date startTime; // Start time of the show
  private final Integer durationInMinutes; // Duration of the show in minutes
}
