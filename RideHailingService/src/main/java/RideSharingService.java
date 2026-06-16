import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import enums.DriverStatus;
import enums.RideType;
import enums.TripStatus;
import lombok.Setter;
import models.Driver;
import models.Location;
import models.Rider;
import models.Trip;
import models.Vehicle;
import strategies.driver.DriverMatchingStrategy;
import strategies.driver.NearestDriverMatchingStrategy;
import strategies.pricing.PricingStrategy;
import strategies.pricing.VehicleBasedPricingStrategy;

public class RideSharingService {
    private static volatile RideSharingService instance;
    private final Map<String, Rider> riders = new ConcurrentHashMap<>();
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private final Map<String, Trip> trips = new ConcurrentHashMap<>();
    // Allow changing strategies at runtime for extensibility
    @Setter
    private PricingStrategy pricingStrategy;
    @Setter
    private DriverMatchingStrategy driverMatchingStrategy;

    private RideSharingService() {
        this.pricingStrategy = new VehicleBasedPricingStrategy();
        this.driverMatchingStrategy = new NearestDriverMatchingStrategy();
    }

    public static synchronized RideSharingService getInstance() {
        if (instance == null) {
            instance = new RideSharingService();
        }
        return instance;
    }

    public Rider registerRider(String name, String contact) {
        Rider rider = new Rider(name, contact);
        riders.put(rider.getId(), rider);
        return rider;
    }

    public Driver registerDriver(String name, String contact, Vehicle vehicle, Location initialLocation) {
        Driver driver = new Driver(name, contact, vehicle, initialLocation);
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public void goOnline(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null)
            throw new NoSuchElementException("Driver not found");
        driver.setStatus(DriverStatus.ONLINE);
    }

    public void goOffline(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null)
            throw new NoSuchElementException("Driver not found");
        driver.setStatus(DriverStatus.OFFLINE);
    }

    public Trip requestRide(String riderId, Location pickup, Location dropoff, RideType rideType) {
        Rider rider = riders.get(riderId);
        if (rider == null)
            throw new NoSuchElementException("Rider not found");

        System.out.println("\n--- New Ride Request from " + rider.getName() + " ---");

        // 1. Find available drivers
        List<Driver> availableDrivers = driverMatchingStrategy.findDrivers(List.copyOf(drivers.values()), pickup, rideType);

        if (availableDrivers.isEmpty()) {
            System.out.println("No drivers available for your request. Please try again later.");
            return null;
        }

        System.out.println("Found " + availableDrivers.size() + " available driver(s).");

        // 2. Calculate fare
        double fare = pricingStrategy.calculateFare(pickup, dropoff, rideType);
        System.out.printf("Estimated fare: $%.2f%n", fare);

        // 3. Create a trip using the Builder
        Trip trip = new Trip.TripBuilder()
                .withRider(rider)
                .withPickupLocation(pickup)
                .withDropoffLocation(dropoff)
                .withFare(fare)
                .build();

        trips.put(trip.getId(), trip);

        // 4. Notify nearby drivers (in a real system, this would be a push notification)
        System.out.println("Notifying nearby drivers of the new ride request...");
        for (Driver driver : availableDrivers) {
            System.out.println(" > Notifying " + driver.getName() + " at " + driver.getCurrentLocation());
            driver.onUpdate(trip);
        }

        return trip;
    }

    public void acceptRide(String driverId, String tripId) {
        Driver driver = drivers.get(driverId);
        Trip trip = trips.get(tripId);
        if (driver == null || trip == null)
            throw new NoSuchElementException("Driver or Trip not found");

        System.out.println("\n--- Driver " + driver.getName() + " accepted the ride ---");

        trip.assignDriver(driver);
        if (trip.getDriver() == driver) { // mark the driver busy only if the assignment was accepted
            driver.setStatus(DriverStatus.IN_TRIP);
        }
    }

    public void startTrip(String tripId) {
        Trip trip = trips.get(tripId);
        if (trip == null)
            throw new NoSuchElementException("Trip not found");
        System.out.println("\n--- Trip " + trip.getId() + " is starting ---");
        trip.startTrip();
    }

    public void endTrip(String tripId) {
        Trip trip = trips.get(tripId);
        if (trip == null)
            throw new NoSuchElementException("Trip not found");
        System.out.println("\n--- Trip " + trip.getId() + " is ending ---");
        trip.endTrip();

        if (trip.getStatus() != TripStatus.COMPLETED) {
            return; // trip not completed; skip finalization
        }

        // Update statuses and history
        Driver driver = trip.getDriver();
        driver.setStatus(DriverStatus.ONLINE); // Driver is available again
        driver.setCurrentLocation(trip.getDropoffLocation()); // Update driver location

        Rider rider = trip.getRider();
        driver.addTripToHistory(trip);
        rider.addTripToHistory(trip);

        System.out.println("Driver " + driver.getName() + " is now back online at " + driver.getCurrentLocation());
    }
}
