package hotelmanagementsystem.model;

import hotelmanagementsystem.enums.ReservationStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class Reservation {
  private final String id;
  private final Guest guest;
  private final Room room;
  private final LocalDate checkInDate;
  private final LocalDate checkOutDate;
  private ReservationStatus status;

  public Reservation(
      String id, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
    this.id = id;
    this.guest = guest;
    this.room = room;
    this.checkInDate = checkInDate;
    this.checkOutDate = checkOutDate;
    this.status = ReservationStatus.CONFIRMED;
  }

  public void cancel() {
    if (status == ReservationStatus.CONFIRMED) {
      status = ReservationStatus.CANCELLED;
      room.checkOut();
    } else {
      throw new IllegalStateException("Reservation is not confirmed.");
    }
  }
}
