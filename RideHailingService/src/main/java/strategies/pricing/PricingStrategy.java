package strategies.pricing;

import enums.RideType;
import models.Location;

public interface PricingStrategy {
    double calculateFare(Location pickup, Location dropoff, RideType rideType);
}
