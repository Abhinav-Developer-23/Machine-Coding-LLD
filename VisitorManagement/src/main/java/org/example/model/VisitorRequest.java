package org.example.model;

import java.time.LocalDateTime;
import org.example.enums.RequestStatus;
import org.example.enums.ResidentDecision;
import org.example.enums.VisitorType;

public class VisitorRequest {
  private final String id;
  private final String societyId;
  private final String houseId;
  private final String visitorName;
  private final VisitorType visitorType;
  private final String purpose;
  private final String requestedBySecurity;
  private final LocalDateTime createdAt;

  private RequestStatus status;
  private ResidentDecision decision;
  private String decidedByResidentId;
  private LocalDateTime decidedAt;

  public VisitorRequest(
      String id,
      String societyId,
      String houseId,
      String visitorName,
      VisitorType visitorType,
      String purpose,
      String requestedBySecurity) {
    this.id = id;
    this.societyId = societyId;
    this.houseId = houseId;
    this.visitorName = visitorName;
    this.visitorType = visitorType;
    this.purpose = purpose;
    this.requestedBySecurity = requestedBySecurity;
    this.createdAt = LocalDateTime.now();
    this.status = RequestStatus.PENDING;
  }

  public String getId() {
    return id;
  }

  public String getSocietyId() {
    return societyId;
  }

  public String getHouseId() {
    return houseId;
  }

  public String getVisitorName() {
    return visitorName;
  }

  public VisitorType getVisitorType() {
    return visitorType;
  }

  public String getPurpose() {
    return purpose;
  }

  public String getRequestedBySecurity() {
    return requestedBySecurity;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public ResidentDecision getDecision() {
    return decision;
  }

  public String getDecidedByResidentId() {
    return decidedByResidentId;
  }

  public LocalDateTime getDecidedAt() {
    return decidedAt;
  }

  public boolean isPending() {
    return status == RequestStatus.PENDING;
  }

  public void markDecision(ResidentDecision residentDecision, String residentId) {
    this.decision = residentDecision;
    this.decidedByResidentId = residentId;
    this.decidedAt = LocalDateTime.now();
    this.status =
        switch (residentDecision) {
          case APPROVE -> RequestStatus.APPROVED;
          case DENY -> RequestStatus.DENIED;
          case LEAVE_AT_GATE -> RequestStatus.LEFT_AT_GATE;
        };
  }

  @Override
  public String toString() {
    return "VisitorRequest{id='%s', visitor='%s', type=%s, status=%s, decision=%s}"
        .formatted(id, visitorName, visitorType, status, decision);
  }
}
