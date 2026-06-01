package org.example.strategies;

import java.util.List;

import org.example.models.Seat;
import org.example.models.Show;
import org.example.models.User;

/**
 * ISeatLockProvider Interface
 *
 * <p>Defines the contract for any class that provides seat locking functionality in the movie
 * ticket booking system. Different locking mechanisms can be used interchangeably within the
 * BookingService (Strategy Pattern).
 */
public interface ISeatLockProvider {
  void lockSeats(Show show, List<Seat> seat, User user) throws Exception;

  void unlockSeats(Show show, List<Seat> seat, User user);

  boolean validateLock(Show show, Seat seat, User user);

  List<Seat> getLockedSeats(Show show);
}
