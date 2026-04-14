package org.example;

import java.util.List;

import org.example.enums.ResidentDecision;
import org.example.enums.VisitorType;
import org.example.model.House;
import org.example.model.Resident;
import org.example.model.Society;
import org.example.model.VisitorRequest;

public class VisitorManagementDemo {
  public static void main(String[] args) {
    VisitorManagementService service = new VisitorManagementService();

    System.out.println("=== Register Societies and Houses ===");
    Society greenHeights = service.registerSociety("Green Heights");
    Society palmResidency = service.registerSociety("Palm Residency");
    House a101 = service.addHouse(greenHeights.getId(), "A-101");
    House b202 = service.addHouse(greenHeights.getId(), "B-202");
    service.addHouse(palmResidency.getId(), "P-11");

    System.out.println(greenHeights);
    System.out.println(palmResidency);
    System.out.println(a101);
    System.out.println(b202);

    System.out.println("\n=== Admin Registers Residents ===");
    Resident rakesh = service.registerResident(greenHeights.getId(), "A-101", "Rakesh");
    Resident meera = service.registerResident(greenHeights.getId(), "A-101", "Meera");
    Resident kabir = service.registerResident(greenHeights.getId(), "B-202", "Kabir");

    System.out.println(rakesh);
    System.out.println(meera);
    System.out.println(kabir);

    System.out.println("\n=== Security Creates Visitor Requests ===");
    VisitorRequest guestRequest =
        service.createVisitorRequest(
            greenHeights.getId(),
            "A-101",
            "Arjun",
            VisitorType.GUEST,
            "Friend visit",
            "Security-Gate-1");
    VisitorRequest visitingHelpRequest =
        service.createVisitorRequest(
            greenHeights.getId(),
            "A-101",
            "Suman",
            VisitorType.VISITING_HELP,
            "Part-time cook",
            "Security-Gate-1");
    VisitorRequest deliveryRequest =
        service.createVisitorRequest(
            greenHeights.getId(),
            "B-202",
            "Zomato Rider",
            VisitorType.DELIVERY,
            "Food delivery",
            "Security-Gate-2");

    List<VisitorRequest> pendingForA101 =
        service.getPendingRequestsForHouse(greenHeights.getId(), "A-101");
    System.out.println("Pending A-101 requests: " + pendingForA101);

    System.out.println("\n=== Residents Decide Requests ===");
    service.decideOnRequest(guestRequest.getId(), rakesh.getId(), ResidentDecision.APPROVE);
    service.decideOnRequest(visitingHelpRequest.getId(), meera.getId(), ResidentDecision.DENY);
    service.decideOnRequest(deliveryRequest.getId(), kabir.getId(), ResidentDecision.LEAVE_AT_GATE);

    System.out.println(guestRequest);
    System.out.println(visitingHelpRequest);
    System.out.println(deliveryRequest);
  }
}
