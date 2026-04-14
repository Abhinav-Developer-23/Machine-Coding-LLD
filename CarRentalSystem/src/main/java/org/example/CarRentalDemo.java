package org.example;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

// ─────────────────────────────────────────────
// Enums
// ─────────────────────────────────────────────

enum VehicleType {
  ECONOMY, // Budget-friendly cars
  COMPACT, // Small cars for city driving
  SUV, // Sport utility vehicles
  LUXURY, // Premium vehicles
  VAN // Large vehicles for groups
}

enum VehicleStatus {
  AVAILABLE, // Ready to rent
  RESERVED, // Assigned to an upcoming reservation
  RENTED, // Currently with a customer
  UNDER_MAINTENANCE // Being serviced
}

enum ReservationStatus {
  CONFIRMED, // Reservation created and confirmed
  ACTIVE, // Customer has picked up the vehicle
  COMPLETED, // Vehicle returned, rental finished
  CANCELLED // Customer cancelled before pickup
}

enum EquipmentType {
  GPS, // Navigation device
  CHILD_SEAT, // Car seat for children
  INSURANCE // Additional coverage
}

enum PaymentMethod {
  CREDIT_CARD,
  DEBIT_CARD,
  CASH
}

enum DamageLevel {
  NONE,
  MINOR,
  MODERATE,
  SEVERE
}

enum LoyaltyTier {
  BRONZE(0.05), // 5% discount
  SILVER(0.10), // 10% discount
  GOLD(0.15); // 15% discount

  private final double discount;

  LoyaltyTier(double discount) {
    this.discount = discount;
  }

  public double getDiscount() {
    return discount;
  }
}

// ─────────────────────────────────────────────
// Exception
// ─────────────────────────────────────────────

class CarRentalException extends RuntimeException {
  public CarRentalException(String message) {
    super(message);
  }
}

// ─────────────────────────────────────────────
// Domain models
// ─────────────────────────────────────────────

class Customer {
  private final String id;
  private final String name;
  private final String email;
  private final String drivingLicense;

  public Customer(String id, String name, String email, String drivingLicense) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.drivingLicense = drivingLicense;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getDrivingLicense() {
    return drivingLicense;
  }

  @Override
  public String toString() {
    return name;
  }
}

class Location {
  private final String id;
  private final String name;
  private final String address;

  public Location(String id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  @Override
  public String toString() {
    return name;
  }
}

class Vehicle {
  private final String id;
  private final String licensePlate;
  private final VehicleType vehicleType;
  private final double dailyRate;
  private VehicleStatus status;
  private String locationId;

  public Vehicle(
      String id,
      String licensePlate,
      VehicleType vehicleType,
      double dailyRate,
      String locationId) {
    this.id = id;
    this.licensePlate = licensePlate;
    this.vehicleType = vehicleType;
    this.dailyRate = dailyRate;
    this.status = VehicleStatus.AVAILABLE;
    this.locationId = locationId;
  }

  public String getId() {
    return id;
  }

  public String getLicensePlate() {
    return licensePlate;
  }

  public VehicleType getVehicleType() {
    return vehicleType;
  }

  public double getDailyRate() {
    return dailyRate;
  }

  public VehicleStatus getStatus() {
    return status;
  }

  public String getLocationId() {
    return locationId;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  @Override
  public String toString() {
    return "Vehicle{id="
        + id
        + ", plate="
        + licensePlate
        + ", type="
        + vehicleType
        + ", rate=$"
        + dailyRate
        + "/day}";
  }
}

class Equipment {
  private final EquipmentType type;
  private final double dailyRate;

  public Equipment(EquipmentType type, double dailyRate) {
    this.type = type;
    this.dailyRate = dailyRate;
  }

  public EquipmentType getType() {
    return type;
  }

  public double getDailyRate() {
    return dailyRate;
  }

  @Override
  public String toString() {
    return type + " ($" + dailyRate + "/day)";
  }
}

class DamageReport {
  private final String vehicleId;
  private final String reservationId;
  private final DamageLevel levelAtPickup;
  private final DamageLevel levelAtReturn;
  private final String notes;

  public DamageReport(
      String vehicleId,
      String reservationId,
      DamageLevel levelAtPickup,
      DamageLevel levelAtReturn,
      String notes) {
    this.vehicleId = vehicleId;
    this.reservationId = reservationId;
    this.levelAtPickup = levelAtPickup;
    this.levelAtReturn = levelAtReturn;
    this.notes = notes;
  }

  public String getVehicleId() {
    return vehicleId;
  }

  public String getReservationId() {
    return reservationId;
  }

  public DamageLevel getLevelAtPickup() {
    return levelAtPickup;
  }

  public DamageLevel getLevelAtReturn() {
    return levelAtReturn;
  }

  public String getNotes() {
    return notes;
  }

  public boolean hasNewDamage() {
    return levelAtReturn.ordinal() > levelAtPickup.ordinal();
  }
}

// ─────────────────────────────────────────────
// Reservation
// ─────────────────────────────────────────────

class Reservation {
  private final String id;
  private final Customer customer;
  private final VehicleType vehicleType;
  private final String pickupLocationId;
  private final String returnLocationId;
  private final LocalDate pickupDate;
  private final LocalDate returnDate;
  private final List<Equipment> equipment;
  private Vehicle assignedVehicle;
  private ReservationStatus status;
  private double totalCost;

  public Reservation(
      String id,
      Customer customer,
      VehicleType vehicleType,
      String pickupLocationId,
      String returnLocationId,
      LocalDate pickupDate,
      LocalDate returnDate,
      List<Equipment> equipment) {
    this.id = id;
    this.customer = customer;
    this.vehicleType = vehicleType;
    this.pickupLocationId = pickupLocationId;
    this.returnLocationId = returnLocationId;
    this.pickupDate = pickupDate;
    this.returnDate = returnDate;
    this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
    this.assignedVehicle = null;
    this.status = ReservationStatus.CONFIRMED;
    this.totalCost = 0;
  }

  public String getId() {
    return id;
  }

  public Customer getCustomer() {
    return customer;
  }

  public VehicleType getVehicleType() {
    return vehicleType;
  }

  public String getPickupLocationId() {
    return pickupLocationId;
  }

  public String getReturnLocationId() {
    return returnLocationId;
  }

  public LocalDate getPickupDate() {
    return pickupDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public List<Equipment> getEquipment() {
    return equipment;
  }

  public Vehicle getAssignedVehicle() {
    return assignedVehicle;
  }

  public ReservationStatus getStatus() {
    return status;
  }

  public double getTotalCost() {
    return totalCost;
  }

  public void assignVehicle(Vehicle vehicle) {
    this.assignedVehicle = vehicle;
  }

  public void activate() {
    if (status != ReservationStatus.CONFIRMED) {
      throw new CarRentalException("Can only activate CONFIRMED reservations. Current: " + status);
    }
    this.status = ReservationStatus.ACTIVE;
  }

  public void complete(double totalCost) {
    if (status != ReservationStatus.ACTIVE) {
      throw new CarRentalException("Can only complete ACTIVE reservations. Current: " + status);
    }
    this.status = ReservationStatus.COMPLETED;
    this.totalCost = totalCost;
  }

  public void cancel() {
    if (status != ReservationStatus.CONFIRMED) {
      throw new CarRentalException("Can only cancel CONFIRMED reservations. Current: " + status);
    }
    this.status = ReservationStatus.CANCELLED;
  }

  @Override
  public String toString() {
    return "Reservation{id="
        + id
        + ", customer="
        + customer.getName()
        + ", type="
        + vehicleType
        + ", status="
        + status
        + "}";
  }
}

// ─────────────────────────────────────────────
// Bill
// ─────────────────────────────────────────────

class Bill {
  private final Reservation reservation;
  private final double baseCost;
  private final double equipmentCost;
  private final double lateFee;
  private final double totalCost;

  public Bill(Reservation reservation, double baseCost, double equipmentCost, double lateFee) {
    this.reservation = reservation;
    this.baseCost = baseCost;
    this.equipmentCost = equipmentCost;
    this.lateFee = lateFee;
    this.totalCost = baseCost + equipmentCost + lateFee;
  }

  public Reservation getReservation() {
    return reservation;
  }

  public double getBaseCost() {
    return baseCost;
  }

  public double getEquipmentCost() {
    return equipmentCost;
  }

  public double getLateFee() {
    return lateFee;
  }

  public double getTotalCost() {
    return totalCost;
  }

  @Override
  public String toString() {
    return String.format(
        "Bill{base=$%.2f, equipment=$%.2f, lateFee=$%.2f, total=$%.2f}",
        baseCost, equipmentCost, lateFee, totalCost);
  }
}

// ─────────────────────────────────────────────
// Pricing strategies
// ─────────────────────────────────────────────

interface PricingStrategy {
  double calculateCost(double dailyRate, int days);
}

class StandardPricingStrategy implements PricingStrategy {
  @Override
  public double calculateCost(double dailyRate, int days) {
    return dailyRate * days;
  }
}

class WeekendPricingStrategy implements PricingStrategy {
  private final double weekendMultiplier;

  public WeekendPricingStrategy(double weekendMultiplier) {
    this.weekendMultiplier = weekendMultiplier;
  }

  @Override
  public double calculateCost(double dailyRate, int days) {
    return dailyRate * days * weekendMultiplier;
  }
}

class SurgePricingStrategy implements PricingStrategy {
  private final double surgeMultiplier;

  public SurgePricingStrategy(double surgeMultiplier) {
    this.surgeMultiplier = surgeMultiplier;
  }

  @Override
  public double calculateCost(double dailyRate, int days) {
    return dailyRate * days * surgeMultiplier;
  }
}

class LoyaltyPricingStrategy implements PricingStrategy {
  private final PricingStrategy baseStrategy;
  private final LoyaltyTier tier;

  public LoyaltyPricingStrategy(PricingStrategy baseStrategy, LoyaltyTier tier) {
    this.baseStrategy = baseStrategy;
    this.tier = tier;
  }

  @Override
  public double calculateCost(double dailyRate, int days) {
    double baseCost = baseStrategy.calculateCost(dailyRate, days);
    return baseCost * (1 - tier.getDiscount());
  }
}

// ─────────────────────────────────────────────
// Observer pattern
// ─────────────────────────────────────────────

interface RentalObserver {
  void onReservationCreated(Reservation reservation);

  void onVehiclePickedUp(Reservation reservation);

  void onVehicleReturned(Reservation reservation, Bill bill);
}

class EmailNotificationObserver implements RentalObserver {
  @Override
  public void onReservationCreated(Reservation reservation) {
    System.out.println(
        "[Email] Reservation confirmed: "
            + reservation.getId()
            + " for "
            + reservation.getCustomer().getName()
            + " - "
            + reservation.getVehicleType()
            + " ("
            + reservation.getPickupDate()
            + " to "
            + reservation.getReturnDate()
            + ")");
  }

  @Override
  public void onVehiclePickedUp(Reservation reservation) {
    System.out.println(
        "[Email] Vehicle picked up: "
            + reservation.getCustomer().getName()
            + " picked up "
            + reservation.getAssignedVehicle().getLicensePlate()
            + " ("
            + reservation.getVehicleType()
            + ")");
  }

  @Override
  public void onVehicleReturned(Reservation reservation, Bill bill) {
    System.out.println(
        "[Email] Vehicle returned: "
            + reservation.getCustomer().getName()
            + " returned "
            + reservation.getAssignedVehicle().getLicensePlate()
            + ". Total: $"
            + String.format("%.2f", bill.getTotalCost()));
  }
}

class InvoiceObserver implements RentalObserver {
  @Override
  public void onReservationCreated(Reservation reservation) {
    // No invoice needed at reservation time
  }

  @Override
  public void onVehiclePickedUp(Reservation reservation) {
    // No invoice needed at pickup time
  }

  @Override
  public void onVehicleReturned(Reservation reservation, Bill bill) {
    System.out.println(
        "[Invoice] Invoice generated for "
            + reservation.getId()
            + ": Base=$"
            + String.format("%.2f", bill.getBaseCost())
            + ", Equipment=$"
            + String.format("%.2f", bill.getEquipmentCost())
            + ", Late Fee=$"
            + String.format("%.2f", bill.getLateFee())
            + ", Total=$"
            + String.format("%.2f", bill.getTotalCost()));
  }
}

// ─────────────────────────────────────────────
// Core system (Singleton)
// ─────────────────────────────────────────────

class CarRentalSystem {
  private static volatile CarRentalSystem instance;
  private static final Object lock = new Object();

  private final ConcurrentHashMap<String, Location> locations;
  private final ConcurrentHashMap<String, Vehicle> vehicles;
  private final ConcurrentHashMap<String, Reservation> reservations;
  // Maps location ID → list of vehicles at that location
  private final ConcurrentHashMap<String, List<Vehicle>> locationVehicles;
  private final CopyOnWriteArrayList<RentalObserver> observers;
  private PricingStrategy pricingStrategy;
  private final AtomicInteger reservationCounter;
  private static final double LATE_FEE_PER_DAY = 50.0;

  private CarRentalSystem() {
    this.locations = new ConcurrentHashMap<>();
    this.vehicles = new ConcurrentHashMap<>();
    this.reservations = new ConcurrentHashMap<>();
    this.locationVehicles = new ConcurrentHashMap<>();
    this.observers = new CopyOnWriteArrayList<>();
    this.pricingStrategy = new StandardPricingStrategy();
    this.reservationCounter = new AtomicInteger(0);
  }

  public static CarRentalSystem getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new CarRentalSystem();
        }
      }
    }
    return instance;
  }

  public synchronized void addLocation(Location location) {
    locations.put(location.getId(), location);
    locationVehicles.putIfAbsent(location.getId(), new ArrayList<>());
  }

  public synchronized void addVehicle(Vehicle vehicle) {
    vehicles.put(vehicle.getId(), vehicle);
    locationVehicles.computeIfAbsent(vehicle.getLocationId(), k -> new ArrayList<>()).add(vehicle);
  }

  public synchronized Reservation makeReservation(
      Customer customer,
      VehicleType vehicleType,
      String pickupLocationId,
      String returnLocationId,
      LocalDate pickupDate,
      LocalDate returnDate,
      List<Equipment> equipment) {
    List<Vehicle> vehiclesAtLocation =
        locationVehicles.getOrDefault(pickupLocationId, Collections.emptyList());
    boolean hasAvailable =
        vehiclesAtLocation.stream()
            .anyMatch(
                v -> v.getVehicleType() == vehicleType && v.getStatus() == VehicleStatus.AVAILABLE);

    if (!hasAvailable) {
      throw new CarRentalException(
          "No " + vehicleType + " vehicles available at location " + pickupLocationId);
    }

    String reservationId = "RES-" + reservationCounter.incrementAndGet();
    Reservation reservation =
        new Reservation(
            reservationId,
            customer,
            vehicleType,
            pickupLocationId,
            returnLocationId,
            pickupDate,
            returnDate,
            equipment);

    reservations.put(reservationId, reservation);
    notifyReservationCreated(reservation);
    return reservation;
  }

  public synchronized Vehicle pickupVehicle(String reservationId) {
    Reservation reservation = reservations.get(reservationId);
    if (reservation == null) {
      throw new CarRentalException("Reservation not found: " + reservationId);
    }
    if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
      throw new CarRentalException(
          "Reservation is not in CONFIRMED status: " + reservation.getStatus());
    }

    // The find-assign-rent sequence is atomic (whole method is synchronized)
    List<Vehicle> vehiclesAtLocation =
        locationVehicles.getOrDefault(reservation.getPickupLocationId(), Collections.emptyList());
    Vehicle vehicle =
        vehiclesAtLocation.stream()
            .filter(
                v ->
                    v.getVehicleType() == reservation.getVehicleType()
                        && v.getStatus() == VehicleStatus.AVAILABLE)
            .findFirst()
            .orElseThrow(
                () ->
                    new CarRentalException(
                        "No available " + reservation.getVehicleType() + " at pickup location"));

    vehicle.setStatus(VehicleStatus.RENTED);
    reservation.assignVehicle(vehicle);
    reservation.activate();
    notifyVehiclePickedUp(reservation);
    return vehicle;
  }

  public synchronized Bill returnVehicle(
      String reservationId, String returnLocationId, LocalDate actualReturnDate) {
    Reservation reservation = reservations.get(reservationId);
    if (reservation == null) {
      throw new CarRentalException("Reservation not found: " + reservationId);
    }
    if (reservation.getStatus() != ReservationStatus.ACTIVE) {
      throw new CarRentalException("Reservation is not ACTIVE: " + reservation.getStatus());
    }

    Vehicle vehicle = reservation.getAssignedVehicle();

    int rawDays =
        (int) (reservation.getReturnDate().toEpochDay() - reservation.getPickupDate().toEpochDay());
    final int rentalDays = rawDays <= 0 ? 1 : rawDays;

    double baseCost = pricingStrategy.calculateCost(vehicle.getDailyRate(), rentalDays);

    double equipmentCost =
        reservation.getEquipment().stream().mapToDouble(e -> e.getDailyRate() * rentalDays).sum();

    double lateFee = 0;
    if (actualReturnDate.isAfter(reservation.getReturnDate())) {
      int lateDays =
          (int) (actualReturnDate.toEpochDay() - reservation.getReturnDate().toEpochDay());
      lateFee = lateDays * LATE_FEE_PER_DAY;
    }

    Bill bill = new Bill(reservation, baseCost, equipmentCost, lateFee);

    vehicle.setStatus(VehicleStatus.AVAILABLE);
    if (!vehicle.getLocationId().equals(returnLocationId)) {
      locationVehicles.get(vehicle.getLocationId()).remove(vehicle);
      vehicle.setLocationId(returnLocationId);
      locationVehicles.computeIfAbsent(returnLocationId, k -> new ArrayList<>()).add(vehicle);
    }

    reservation.complete(bill.getTotalCost());
    notifyVehicleReturned(reservation, bill);
    return bill;
  }

  public synchronized void cancelReservation(String reservationId) {
    Reservation reservation = reservations.get(reservationId);
    if (reservation == null) {
      throw new CarRentalException("Reservation not found: " + reservationId);
    }
    reservation.cancel();
  }

  /** Transfer an AVAILABLE vehicle between locations (e.g. fleet rebalancing). */
  public synchronized void transferVehicle(
      String vehicleId, String fromLocationId, String toLocationId) {
    Vehicle vehicle = vehicles.get(vehicleId);
    if (vehicle == null) {
      throw new CarRentalException("Vehicle not found: " + vehicleId);
    }
    if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
      throw new CarRentalException(
          "Can only transfer AVAILABLE vehicles. Current: " + vehicle.getStatus());
    }
    locationVehicles.get(fromLocationId).remove(vehicle);
    vehicle.setLocationId(toLocationId);
    locationVehicles.computeIfAbsent(toLocationId, k -> new ArrayList<>()).add(vehicle);
  }

  public synchronized void setPricingStrategy(PricingStrategy strategy) {
    this.pricingStrategy = strategy;
  }

  public void addObserver(RentalObserver observer) {
    observers.add(observer);
  }

  public void removeObserver(RentalObserver observer) {
    observers.remove(observer);
  }

  private void notifyReservationCreated(Reservation reservation) {
    for (RentalObserver observer : observers) {
      try {
        observer.onReservationCreated(reservation);
      } catch (Exception e) {
        System.err.println("Observer notification failed: " + e.getMessage());
      }
    }
  }

  private void notifyVehiclePickedUp(Reservation reservation) {
    for (RentalObserver observer : observers) {
      try {
        observer.onVehiclePickedUp(reservation);
      } catch (Exception e) {
        System.err.println("Observer notification failed: " + e.getMessage());
      }
    }
  }

  private void notifyVehicleReturned(Reservation reservation, Bill bill) {
    for (RentalObserver observer : observers) {
      try {
        observer.onVehicleReturned(reservation, bill);
      } catch (Exception e) {
        System.err.println("Observer notification failed: " + e.getMessage());
      }
    }
  }
}

// ─────────────────────────────────────────────
// Demo
// ─────────────────────────────────────────────

public class CarRentalDemo {
  public static void main(String[] args) {
    CarRentalSystem system = CarRentalSystem.getInstance();

    // Register observers
    system.addObserver(new EmailNotificationObserver());
    system.addObserver(new InvoiceObserver());

    // Add locations
    Location jfk = new Location("L1", "JFK Airport", "JFK Airport, NY");
    Location downtown = new Location("L2", "Downtown Manhattan", "123 Main St, NY");
    system.addLocation(jfk);
    system.addLocation(downtown);

    // Add vehicles
    system.addVehicle(new Vehicle("V1", "ABC-1234", VehicleType.ECONOMY, 40.0, "L1"));
    system.addVehicle(new Vehicle("V2", "DEF-5678", VehicleType.ECONOMY, 40.0, "L1"));
    system.addVehicle(new Vehicle("V3", "GHI-9012", VehicleType.SUV, 75.0, "L1"));
    system.addVehicle(new Vehicle("V4", "JKL-3456", VehicleType.LUXURY, 150.0, "L2"));

    // Customers
    Customer alice = new Customer("C1", "Alice", "alice@email.com", "DL-001");
    Customer bob = new Customer("C2", "Bob", "bob@email.com", "DL-002");

    // Equipment options
    Equipment gps = new Equipment(EquipmentType.GPS, 10.0);
    Equipment childSeat = new Equipment(EquipmentType.CHILD_SEAT, 8.0);

    // ── Scenario 1: Standard reservation and pickup ──
    System.out.println("========== SCENARIO 1: Reserve + Pickup (Standard Pricing) ==========");
    system.setPricingStrategy(new StandardPricingStrategy());
    Reservation res1 =
        system.makeReservation(
            alice,
            VehicleType.ECONOMY,
            "L1",
            "L1",
            LocalDate.of(2025, 3, 10),
            LocalDate.of(2025, 3, 13),
            Arrays.asList(gps));
    System.out.println("Reserved: " + res1);

    Vehicle pickup1 = system.pickupVehicle(res1.getId());
    System.out.println("Picked up: " + pickup1);

    // ── Scenario 2: Return on time ──
    System.out.println("\n========== SCENARIO 2: Return On Time ==========");
    Bill bill1 = system.returnVehicle(res1.getId(), "L1", LocalDate.of(2025, 3, 13));
    System.out.println("Bill: " + bill1);

    // ── Scenario 3: Weekend pricing ──
    System.out.println("\n========== SCENARIO 3: Reserve with Weekend Pricing ==========");
    system.setPricingStrategy(new WeekendPricingStrategy(1.5));
    Reservation res2 =
        system.makeReservation(
            bob,
            VehicleType.SUV,
            "L1",
            "L2",
            LocalDate.of(2025, 3, 15),
            LocalDate.of(2025, 3, 17),
            Arrays.asList(gps, childSeat));
    System.out.println("Reserved: " + res2);

    Vehicle pickup2 = system.pickupVehicle(res2.getId());
    System.out.println("Picked up: " + pickup2);

    // ── Scenario 4: Late return ──
    System.out.println("\n========== SCENARIO 4: Late Return (1 day late) ==========");
    Bill bill2 = system.returnVehicle(res2.getId(), "L2", LocalDate.of(2025, 3, 18));
    System.out.println("Bill: " + bill2);

    // ── Scenario 5: Cancel a reservation ──
    System.out.println("\n========== SCENARIO 5: Cancel Reservation ==========");
    Reservation res3 =
        system.makeReservation(
            alice,
            VehicleType.LUXURY,
            "L2",
            "L2",
            LocalDate.of(2025, 4, 1),
            LocalDate.of(2025, 4, 5),
            Collections.emptyList());
    System.out.println("Reserved: " + res3);
    system.cancelReservation(res3.getId());
    System.out.println("Cancelled. Status: " + res3.getStatus());

    // ── Scenario 6: Vehicle transfer between locations ──
    System.out.println("\n========== SCENARIO 6: Transfer Vehicle (Fleet Rebalancing) ==========");
    system.transferVehicle("V2", "L1", "L2");
    System.out.println("Transferred V2 (ABC-5678) from JFK to Downtown Manhattan");

    // ── Scenario 7: Loyalty pricing ──
    System.out.println("\n========== SCENARIO 7: Loyalty Pricing (Gold tier) ==========");
    system.setPricingStrategy(
        new LoyaltyPricingStrategy(new StandardPricingStrategy(), LoyaltyTier.GOLD));
    Reservation res4 =
        system.makeReservation(
            alice,
            VehicleType.ECONOMY,
            "L1",
            "L1",
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2025, 5, 5),
            Collections.emptyList());
    System.out.println("Reserved: " + res4);
    Vehicle pickup4 = system.pickupVehicle(res4.getId());
    System.out.println("Picked up: " + pickup4);
    Bill bill4 = system.returnVehicle(res4.getId(), "L1", LocalDate.of(2025, 5, 5));
    System.out.println("Bill (15% loyalty discount): " + bill4);

    // ── Scenario 8: Damage report ──
    System.out.println("\n========== SCENARIO 8: Damage Report ==========");
    DamageReport report =
        new DamageReport(
            "V3",
            res2.getId(),
            DamageLevel.NONE,
            DamageLevel.MINOR,
            "Small scratch on rear bumper");
    System.out.println(
        "Damage report for "
            + report.getVehicleId()
            + ": new damage="
            + report.hasNewDamage()
            + " ("
            + report.getLevelAtPickup()
            + " -> "
            + report.getLevelAtReturn()
            + ")"
            + " | Notes: "
            + report.getNotes());
  }
}
