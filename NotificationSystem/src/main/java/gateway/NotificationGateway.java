package gateway;

import models.Notification;

public interface NotificationGateway {
    void send(Notification notification) throws DeliveryException;
}
