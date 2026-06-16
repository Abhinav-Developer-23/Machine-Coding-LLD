package states;

import enums.TripStatus;
import models.Driver;
import models.Trip;

public class CompletedState implements TripState {
    @Override
    public TripStatus getStatus() { return TripStatus.COMPLETED; }

    @Override
    public void assign(Trip trip, Driver driver) {
        System.out.println("Cannot assign a driver to a completed trip.");
    }

    @Override
    public void start(Trip trip) {
        System.out.println("Cannot start a completed trip.");
    }

    @Override
    public void end(Trip trip) {
        System.out.println("Trip is already completed.");
    }
}
