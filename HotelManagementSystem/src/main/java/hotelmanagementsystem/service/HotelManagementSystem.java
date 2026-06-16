package hotelmanagementsystem.service;

import hotelmanagementsystem.enums.ReservationStatus;
import hotelmanagementsystem.enums.RoomStatus;
import hotelmanagementsystem.model.Guest;
import hotelmanagementsystem.model.Reservation;
import hotelmanagementsystem.model.Room;
import hotelmanagementsystem.payment.Payment;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HotelManagementSystem {

  // Thread-safe singleton via double-checked locking
  private static volatile HotelManagementSystem instance;

  private final ConcurrentHashMap<String, Guest> guests;
  private final ConcurrentHashMap<String, Room> rooms;
  private final ConcurrentHashMap<String, Reservation> reservations;

  private HotelManagementSystem() {
    guests = new ConcurrentHashMap<>();
    rooms = new ConcurrentHashMap<>();
    reservations = new ConcurrentHashMap<>();
  }

  public static HotelManagementSystem getInstance() {
    if (instance == null) {
      synchronized (HotelManagementSystem.class) {
        if (instance == null) {
          instance = new HotelManagementSystem();
        }
      }
    }
    return instance;
  }

  // ── Guest management ──────────────────────────────────────────────────────

  public void addGuest(Guest guest) {
    guests.put(guest.getId(), guest);
  }

  public Guest getGuest(String guestId) {
    return guests.get(guestId);
  }

  // ── Room management ───────────────────────────────────────────────────────

  public void addRoom(Room room) {
    rooms.put(room.getId(), room);
  }

  public Room getRoom(String roomId) {
    return rooms.get(roomId);
  }

  // ── Reservation operations ────────────────────────────────────────────────

  public Reservation bookRoom(
      Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
    synchronized (room) {
      if (room.getStatus() == RoomStatus.AVAILABLE) {
        room.book();
        String reservationId = generateReservationId();
        Reservation reservation =
            new Reservation(reservationId, guest, room, checkInDate, checkOutDate);
        reservations.put(reservationId, reservation);
        return reservation;
      }
      return null;
    }
  }

  public void cancelReservation(String reservationId) {
    synchronized (this) {
      Reservation reservation = reservations.get(reservationId);
      if (reservation != null) {
        reservation.cancel();
        reservations.remove(reservationId);
      }
    }
  }

  public void checkIn(String reservationId) {
    synchronized (this) {
      Reservation reservation = reservations.get(reservationId);
      if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
        reservation.getRoom().checkIn();
      } else {
        throw new IllegalStateException("Invalid reservation or reservation not confirmed.");
      }
    }
  }

  public void checkOut(String reservationId, Payment payment) {
    synchronized (this) {
      Reservation reservation = reservations.get(reservationId);
      if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
        Room room = reservation.getRoom();
        long nights =
            ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        double amount = room.getPrice() * nights;
        if (payment.processPayment(amount)) {
          room.checkOut();
          reservations.remove(reservationId);
        } else {
          throw new IllegalStateException("Payment failed.");
        }
      } else {
        throw new IllegalStateException("Invalid reservation or reservation not confirmed.");
      }
    }
  }

  // ── Utilities ─────────────────────────────────────────────────────────────

  private String generateReservationId() {
    return "RES" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
  }
}
