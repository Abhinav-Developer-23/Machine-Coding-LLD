package org.example.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.example.models.Movie;
import org.example.models.Screen;
import org.example.models.Seat;
import org.example.models.Show;
import org.example.services.MovieService;
import org.example.services.SeatAvailabilityService;
import org.example.services.ShowService;
import org.example.services.TheatreService;

public class ShowController {
  // Dependencies injected for handling show operations, theatre data, movie data, and seat
  // availability.
  private final SeatAvailabilityService seatAvailabilityService;
  private final ShowService showService;
  private final TheatreService theatreService;
  private final MovieService movieService;

  // Constructor to inject all required services
  public ShowController(
      SeatAvailabilityService seatAvailabilityService,
      ShowService showService,
      TheatreService theatreService,
      MovieService movieService) {
    this.seatAvailabilityService = seatAvailabilityService;
    this.showService = showService;
    this.theatreService = theatreService;
    this.movieService = movieService;
  }

  public int createShow(
      final int movieId, final int screenId, final Date startTime, final Integer durationInSeconds)
      throws Exception {
    final Screen screen = theatreService.getScreen(screenId);
    final Movie movie = movieService.getMovie(movieId);
    return showService.createShow(movie, screen, startTime, durationInSeconds).getId();
  }

  public List<Integer> getAvailableSeats(final int showId) throws Exception {
    final Show show = showService.getShow(showId);
    final List<Seat> availableSeats = seatAvailabilityService.getAvailableSeats(show);
    return availableSeats.stream().map(Seat::getSeatId).collect(Collectors.toList());
  }
}
