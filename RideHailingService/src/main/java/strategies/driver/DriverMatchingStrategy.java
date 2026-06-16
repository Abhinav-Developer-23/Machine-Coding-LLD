package strategies.driver;

import java.util.List;

import enums.RideType;
import models.Driver;
import models.Location;

public interface DriverMatchingStrategy {
    List<Driver> findDrivers(List<Driver> allDrivers, Location pickupLocation, RideType rideType);
}
