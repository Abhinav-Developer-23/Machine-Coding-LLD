package models;

import enums.RideType;
import lombok.Getter;

@Getter
public class Vehicle {
    private final String licenseNumber;
    private final String model;
    private final RideType type;

    public Vehicle(String licenseNumber, String model, RideType type) {
        this.licenseNumber = licenseNumber;
        this.model = model;
        this.type = type;
    }
}
