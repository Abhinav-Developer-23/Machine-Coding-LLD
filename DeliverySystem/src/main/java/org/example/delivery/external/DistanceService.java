package org.example.delivery.external;

public class DistanceService {

  public int getDistance(String source, String destination) {
    return Math.abs(source.hashCode() - destination.hashCode()) % 1000;
  }
}
