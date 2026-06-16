package models;

import enums.DriverStatus;
import enums.TripStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Driver extends User {
    private Vehicle vehicle;
    @Setter private Location currentLocation;
    private DriverStatus status;

    public Driver(String name, String contact, Vehicle vehicle, Location initialLocation) {
        super(name, contact);
        this.vehicle = vehicle;
        this.currentLocation = initialLocation;
        this.status = DriverStatus.OFFLINE; // Default status
    }

    // Custom setter — has side effect (logging), so not using @Setter
    public void setStatus(DriverStatus status) {
        this.status = status;
        System.out.println("Driver " + getName() + " is now " + status);
    }

    @Override
    public void onUpdate(Trip trip) {
        System.out.printf("--- Notification for Driver %s ---\n", getName());
        System.out.printf("  Trip %s status: %s.\n", trip.getId(), trip.getStatus());
        if (trip.getStatus() == TripStatus.REQUESTED) {
            System.out.println("  A new ride is available for you to accept.");
        }
        System.out.println("--------------------------------\n");
    }
}
