package org.example.models;

import java.time.Instant;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a temporary lock on a specific seat. These locks are acquired by users during the
 * booking process to prevent concurrent bookings of the same seat by multiple users.
 */
@Getter
@Setter
public class SeatLock {
  private Seat seat; // The specific seat that is locked.
  private Show show; // The show for which the seat is locked.
  private Integer timeoutInSeconds; // The duration for which the lock is valid, in seconds.
  private Date lockTime; // The timestamp when the lock was acquired.
  private User lockedBy; // Identifier of the user or process that holds the lock.

  public SeatLock(Seat seat, Show show, Integer timeoutInSeconds, Date date, User user) {
    this.seat = seat;
    this.show = show;
    this.timeoutInSeconds = timeoutInSeconds;
    this.lockTime = date;
    this.lockedBy = user;
  }

  public boolean isLockExpired() {
    final Instant lockInstant = lockTime.toInstant().plusSeconds(timeoutInSeconds);
    final Instant currentInstant = new Date().toInstant();
    return lockInstant.isBefore(currentInstant);
  }
}
