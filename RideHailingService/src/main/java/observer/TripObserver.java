package observer;

import models.Trip;

public interface TripObserver {
    void onUpdate(Trip trip);
}
