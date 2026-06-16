package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import observer.TripObserver;

@Getter
public abstract class User implements TripObserver {
    private final String id;
    private final String name;
    private final String contact;
    private final List<Trip> tripHistory;

    public User(String name, String contact) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.contact = contact;
        this.tripHistory = new ArrayList<>();
    }

    public void addTripToHistory(Trip trip) {
        tripHistory.add(trip);
    }
}
