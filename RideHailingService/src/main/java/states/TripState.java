package states;

import enums.TripStatus;
import models.Driver;
import models.Trip;

public interface TripState {
    TripStatus getStatus();
    void assign(Trip trip, Driver driver);
    void start(Trip trip);
    void end(Trip trip);
}
