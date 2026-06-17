package factory;

import gateway.EmailGateway;
import gateway.NotificationGateway;
import gateway.PushGateway;
import gateway.SmsGateway;
import models.NotificationType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationFactory {
    private static final Map<NotificationType, NotificationGateway> gatewayMap = new ConcurrentHashMap<>();

    public static NotificationGateway createGateway(NotificationType type) {
        // computeIfAbsent builds the gateway at most once per type, even when
        // several pool threads request the same channel at the same moment.
        return gatewayMap.computeIfAbsent(type, NotificationFactory::buildGateway);
    }

    private static NotificationGateway buildGateway(NotificationType type) {
        return switch (type) {
            case EMAIL -> new EmailGateway();
            case SMS -> new SmsGateway();
            case PUSH -> new PushGateway();
            default -> throw new IllegalArgumentException("Unsupported notification type: " + type);
        };
    }
}
