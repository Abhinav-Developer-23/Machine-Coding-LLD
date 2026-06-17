package gateway;

import models.Notification;

public class SmsGateway implements NotificationGateway {
    @Override
    public void send(Notification notification) {
        String phone = notification.getRecipient().getPhoneNumber()
                .orElseThrow(() -> new IllegalArgumentException("Phone number is required for SMS notification."));
        System.out.println("--- Sending SMS ---");
        System.out.println("To: " + phone);
        System.out.println("Message: " + notification.getMessage());
        System.out.println("-------------------\n");
    }
}
