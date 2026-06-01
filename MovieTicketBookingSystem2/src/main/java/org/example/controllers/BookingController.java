package org.example.controllers;

import java.util.ArrayList;
import java.util.List;

import org.example.models.Seat;
import org.example.models.Show;
import org.example.models.User;
import org.example.services.BookingService;
import org.example.services.ShowService;
import org.example.services.TheatreService;

public class BookingController {
  // Services required to handle booking-related operations
  private final ShowService showService;
  private final BookingService bookingService;
  private final TheatreService theatreService;

  public BookingController(
      final ShowService showService,
      final BookingService bookingService,
      final TheatreService theatreService) {
    this.showService = showService;
    this.bookingService = bookingService;
    this.theatreService = theatreService;
  }

  public String createBooking(final User user, final int showId, final List<Integer> seatsIds)
      throws Exception {
    final Show show = showService.getShow(showId); // Retrieve the show object
    // Convert seat IDs to Seat objects
    final List<Seat> seats = new ArrayList<>();
    for (Integer seatsId : seatsIds) {
      Seat seat = theatreService.getSeat(seatsId);
      seats.add(seat);
    }
    return bookingService.createBooking(user, show, seats).getId(); // Create and return booking ID
  }
}
