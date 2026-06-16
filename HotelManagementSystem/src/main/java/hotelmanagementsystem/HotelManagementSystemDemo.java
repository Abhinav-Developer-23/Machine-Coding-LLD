package hotelmanagementsystem;

import hotelmanagementsystem.enums.RoomType;
import hotelmanagementsystem.model.Guest;
import hotelmanagementsystem.model.Reservation;
import hotelmanagementsystem.model.Room;
import hotelmanagementsystem.payment.CreditCardPayment;
import hotelmanagementsystem.payment.Payment;
import hotelmanagementsystem.service.HotelManagementSystem;
import java.time.LocalDate;

public class HotelManagementSystemDemo {

  public static void main(String[] args) {
    HotelManagementSystem hms = HotelManagementSystem.getInstance();

    // ── Create guests ────────────────────────────────────────────────────────
    Guest guest1 = new Guest("G001", "John Doe", "john@example.com", "1234567890");
    Guest guest2 = new Guest("G002", "Jane Smith", "jane@example.com", "9876543210");
    hms.addGuest(guest1);
    hms.addGuest(guest2);

    // ── Create rooms ─────────────────────────────────────────────────────────
    Room room1 = new Room("R001", RoomType.SINGLE, 100.0);
    Room room2 = new Room("R002", RoomType.DOUBLE, 200.0);
    hms.addRoom(room1);
    hms.addRoom(room2);

    // ── Book a room ──────────────────────────────────────────────────────────
    LocalDate checkInDate = LocalDate.now();
    LocalDate checkOutDate = checkInDate.plusDays(3);

    Reservation reservation1 = hms.bookRoom(guest1, room1, checkInDate, checkOutDate);
    if (reservation1 != null) {
      System.out.println("Reservation created : " + reservation1.getId());
    } else {
      System.out.println("Room not available for booking.");
    }

    // ── Check-in ─────────────────────────────────────────────────────────────
    hms.checkIn(reservation1.getId());
    System.out.println("Checked in          : " + reservation1.getId());

    // ── Check-out with payment ───────────────────────────────────────────────
    Payment payment = new CreditCardPayment();
    hms.checkOut(reservation1.getId(), payment);
    System.out.println("Checked out         : " + reservation1.getId());

    // ── Book room2 for guest2, then cancel ───────────────────────────────────
    Reservation reservation2 =
        hms.bookRoom(guest2, room2, checkInDate, checkOutDate.plusDays(2));
    if (reservation2 != null) {
      System.out.println("Reservation created : " + reservation2.getId());
    }

    hms.cancelReservation(reservation2.getId());
    System.out.println("Reservation cancelled: " + reservation2.getId());
  }
}
