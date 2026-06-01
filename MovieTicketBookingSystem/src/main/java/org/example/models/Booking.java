package org.example.models;

import java.util.List;
import java.util.UUID;

import org.example.enums.SeatStatus;

import lombok.Getter;

@Getter
public class Booking {
  private final String id;
  private final User user;
  private final Show show;
  private final List<Seat> seats;
  private final double totalAmount;
  private final Payment payment;

  // Private constructor to be used by the Builder
  private Booking(
      String id, User user, Show show, List<Seat> seats, double totalAmount, Payment payment) {
    this.id = id;
    this.user = user;
    this.show = show;
    this.seats = seats;
    this.totalAmount = totalAmount;
    this.payment = payment;
  }

  // Marks seats as BOOKED upon successful booking creation
  public void confirmBooking() {
    for (Seat seat : seats) {
      seat.setStatus(SeatStatus.BOOKED);
    }
  }

  // Static inner Builder class
  public static class BookingBuilder {
    private String id;
    private User user;
    private Show show;
    private List<Seat> seats;
    private double totalAmount;
    private Payment payment;

    public BookingBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public BookingBuilder setUser(User user) {
      this.user = user;
      return this;
    }

    public BookingBuilder setShow(Show show) {
      this.show = show;
      return this;
    }

    public BookingBuilder setSeats(List<Seat> seats) {
      this.seats = seats;
      return this;
    }

    public BookingBuilder setTotalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public BookingBuilder setPayment(Payment payment) {
      this.payment = payment;
      return this;
    }

    public Booking build() {
      String bookingId = (id != null) ? id : UUID.randomUUID().toString();
      return new Booking(bookingId, user, show, seats, totalAmount, payment);
    }
  }
}
