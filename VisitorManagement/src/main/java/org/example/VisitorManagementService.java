package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.example.enums.ResidentDecision;
import org.example.enums.VisitorType;
import org.example.model.House;
import org.example.model.Resident;
import org.example.model.Society;
import org.example.model.VisitorRequest;

public class VisitorManagementService {
  private final Map<String, Society> societiesById;
  private final Map<String, House> housesById;
  private final Map<String, Resident> residentsById;
  private final Map<String, VisitorRequest> requestsById;

  private final AtomicInteger societySequence;
  private final AtomicInteger houseSequence;
  private final AtomicInteger residentSequence;
  private final AtomicInteger requestSequence;

  public VisitorManagementService() {
    societiesById = new LinkedHashMap<>();
    housesById = new LinkedHashMap<>();
    residentsById = new LinkedHashMap<>();
    requestsById = new LinkedHashMap<>();
    societySequence = new AtomicInteger(1);
    houseSequence = new AtomicInteger(1);
    residentSequence = new AtomicInteger(1);
    requestSequence = new AtomicInteger(1);
  }

  public Society registerSociety(String societyName) {
    validateText(societyName, "Society name");
    String societyId = "SOC-" + societySequence.getAndIncrement();
    Society society = new Society(societyId, societyName);
    societiesById.put(societyId, society);
    return society;
  }

  public House addHouse(String societyId, String houseNumber) {
    Society society = getSocietyOrThrow(societyId);
    validateText(houseNumber, "House number");

    if (society.hasHouse(houseNumber)) {
      throw new IllegalArgumentException(
          "House already exists in society. houseNumber=" + houseNumber);
    }

    String houseId = "H-" + houseSequence.getAndIncrement();
    House house = new House(houseId, houseNumber, societyId);
    society.addHouse(house);
    housesById.put(houseId, house);
    return house;
  }

  public Resident registerResident(String societyId, String houseNumber, String residentName) {
    House house = getHouseBySocietyAndNumber(societyId, houseNumber);
    validateText(residentName, "Resident name");

    String residentId = "R-" + residentSequence.getAndIncrement();
    Resident resident = new Resident(residentId, residentName, house.getId());
    house.addResident(resident);
    residentsById.put(residentId, resident);
    return resident;
  }

  public VisitorRequest createVisitorRequest(
      String societyId,
      String houseNumber,
      String visitorName,
      VisitorType visitorType,
      String purpose,
      String securityPersonName) {
    House house = getHouseBySocietyAndNumber(societyId, houseNumber);
    validateText(visitorName, "Visitor name");
    validateText(purpose, "Purpose");
    validateText(securityPersonName, "Security person name");
    if (visitorType == null) {
      throw new IllegalArgumentException("Visitor type cannot be null");
    }

    String requestId = "VR-" + requestSequence.getAndIncrement();
    VisitorRequest request =
        new VisitorRequest(
            requestId,
            societyId,
            house.getId(),
            visitorName,
            visitorType,
            purpose,
            securityPersonName);
    requestsById.put(requestId, request);
    return request;
  }

  public VisitorRequest decideOnRequest(
      String requestId, String residentId, ResidentDecision residentDecision) {
    VisitorRequest request = getRequestOrThrow(requestId);
    Resident resident = getResidentOrThrow(residentId);
    if (residentDecision == null) {
      throw new IllegalArgumentException("Resident decision cannot be null");
    }

    if (!request.isPending()) {
      throw new IllegalStateException("Request already decided. requestId=" + requestId);
    }

    House house = getHouseOrThrow(request.getHouseId());
    if (!house.hasResident(resident.getId())) {
      throw new IllegalArgumentException(
          "Resident cannot decide this request. residentId="
              + residentId
              + ", requestHouseId="
              + request.getHouseId());
    }

    validateDecisionForType(request.getVisitorType(), residentDecision);
    request.markDecision(residentDecision, residentId);
    return request;
  }

  public List<VisitorRequest> getRequestsForHouse(String societyId, String houseNumber) {
    House house = getHouseBySocietyAndNumber(societyId, houseNumber);
    return requestsById.values().stream()
        .filter(request -> request.getHouseId().equals(house.getId()))
        .sorted(Comparator.comparing(VisitorRequest::getCreatedAt))
        .toList();
  }

  public List<VisitorRequest> getPendingRequestsForHouse(String societyId, String houseNumber) {
    List<VisitorRequest> pending = new ArrayList<>();
    for (VisitorRequest request : getRequestsForHouse(societyId, houseNumber)) {
      if (request.isPending()) {
        pending.add(request);
      }
    }
    return pending;
  }

  private void validateDecisionForType(VisitorType visitorType, ResidentDecision residentDecision) {
    if (residentDecision == ResidentDecision.LEAVE_AT_GATE && visitorType != VisitorType.DELIVERY) {
      throw new IllegalArgumentException(
          "LEAVE_AT_GATE allowed only for DELIVERY. visitorType=" + visitorType);
    }
  }

  private Society getSocietyOrThrow(String societyId) {
    Society society = societiesById.get(societyId);
    if (society == null) {
      throw new IllegalArgumentException("Society not found. societyId=" + societyId);
    }
    return society;
  }

  private House getHouseBySocietyAndNumber(String societyId, String houseNumber) {
    Society society = getSocietyOrThrow(societyId);
    validateText(houseNumber, "House number");
    House house = society.getHouseByNumber(houseNumber);
    if (house == null) {
      throw new IllegalArgumentException(
          "House not found in society. societyId=" + societyId + ", houseNumber=" + houseNumber);
    }
    return house;
  }

  private House getHouseOrThrow(String houseId) {
    House house = housesById.get(houseId);
    if (house == null) {
      throw new IllegalArgumentException("House not found. houseId=" + houseId);
    }
    return house;
  }

  private Resident getResidentOrThrow(String residentId) {
    Resident resident = residentsById.get(residentId);
    if (resident == null) {
      throw new IllegalArgumentException("Resident not found. residentId=" + residentId);
    }
    return resident;
  }

  private VisitorRequest getRequestOrThrow(String requestId) {
    VisitorRequest request = requestsById.get(requestId);
    if (request == null) {
      throw new IllegalArgumentException("Visitor request not found. requestId=" + requestId);
    }
    return request;
  }

  private void validateText(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or blank");
    }
  }
}
