package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import enums.TripStatus;
import lombok.Getter;
import lombok.Setter;
import observer.TripObserver;
import states.RequestedState;
import states.TripState;

public class Trip {
    @Getter private final String id;
    @Getter private final Rider rider;
    @Getter @Setter private Driver driver;
    @Getter private final Location pickupLocation;
    @Getter private final Location dropoffLocation;
    @Getter private final double fare;

    private TripState currentState;
    private final List<TripObserver> observers = new ArrayList<>();

    private Trip(TripBuilder builder) {
        this.id = builder.id;
        this.rider = builder.rider;
        this.pickupLocation = builder.pickupLocation;
        this.dropoffLocation = builder.dropoffLocation;
        this.fare = builder.fare;
        this.currentState = new RequestedState(); // Initial state
        addObserver(this.rider);
    }

    public void addObserver(TripObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        observers.forEach(o -> o.onUpdate(this));
    }

    public void assignDriver(Driver driver) {
        TripState previousState = currentState;
        currentState.assign(this, driver);
        if (currentState != previousState) { // proceed only if the assignment was accepted
            addObserver(driver);
            notifyObservers();
        }
    }

    public void startTrip() {
        TripState previousState = currentState;
        currentState.start(this);
        if (currentState != previousState) {
            notifyObservers();
        }
    }

    public void endTrip() {
        TripState previousState = currentState;
        currentState.end(this);
        if (currentState != previousState) {
            notifyObservers();
        }
    }

    // Custom getter — delegates to state object, not a simple field access
    public TripStatus getStatus() { return currentState.getStatus(); }

    // Custom setter — field is 'currentState' but method is 'setState'
    public void setState(TripState state) {
        this.currentState = state;
    }

    // --- Builder Pattern ---
    public static class TripBuilder {
        private final String id;
        private Rider rider;
        private Location pickupLocation;
        private Location dropoffLocation;
        private double fare;

        public TripBuilder() {
            this.id = UUID.randomUUID().toString();
        }

        public TripBuilder withRider(Rider rider) {
            this.rider = rider;
            return this;
        }

        public TripBuilder withPickupLocation(Location pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public TripBuilder withDropoffLocation(Location dropoffLocation) {
            this.dropoffLocation = dropoffLocation;
            return this;
        }

        public TripBuilder withFare(double fare) {
            this.fare = fare;
            return this;
        }

        public Trip build() {
            // Basic validation
            if (rider == null || pickupLocation == null || dropoffLocation == null) {
                throw new IllegalStateException("Rider, pickup, and dropoff locations are required to build a trip.");
            }
            return new Trip(this);
        }
    }

    @Override
    public String toString() {
        return "Trip [id=" + id + ", status=" + getStatus() + ", fare=$" + String.format("%.2f", fare) + "]";
    }
}
