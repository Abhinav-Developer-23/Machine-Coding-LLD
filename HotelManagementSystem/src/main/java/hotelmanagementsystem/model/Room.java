package hotelmanagementsystem.model;

import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.enums.RoomType;
import lombok.Getter;

@Getter
public class Room {
  private final String id;
  private final RoomType type;
  private final double price;
  private RoomStatus status;

  public Room(String id, RoomType type, double price) {
    this.id = id;
    this.type = type;
    this.price = price;
    this.status = RoomStatus.AVAILABLE;
  }

  public synchronized void book() {
    if (status == RoomStatus.AVAILABLE) {
      status = RoomStatus.BOOKED;
    } else {
      throw new IllegalStateException("Room is not available for booking.");
    }
  }

  public synchronized void checkIn() {
    if (status == RoomStatus.BOOKED) {
      status = RoomStatus.OCCUPIED;
    } else {
      throw new IllegalStateException("Room is not booked.");
    }
  }

  public synchronized void checkOut() {
    if (status == RoomStatus.OCCUPIED) {
      status = RoomStatus.AVAILABLE;
    } else {
      throw new IllegalStateException("Room is not occupied.");
    }
  }
}
