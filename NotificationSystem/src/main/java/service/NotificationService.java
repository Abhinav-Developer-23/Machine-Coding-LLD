package service;

import decorator.RetryableGatewayDecorator;
import factory.NotificationFactory;
import gateway.NotificationGateway;
import models.Notification;
import models.NotificationType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NotificationService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final ExecutorService executor;
    private final Map<NotificationType, NotificationGateway> retryableGateways = new ConcurrentHashMap<>();

    public NotificationService(int corePoolSize, int maxPoolSize, long keepAliveTimeSecs) {
        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTimeSecs,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    private NotificationGateway gatewayFor(NotificationType type) {
        // Build and cache the retry-wrapped gateway once per channel.
        return retryableGateways.computeIfAbsent(type,
                t -> new RetryableGatewayDecorator(NotificationFactory.createGateway(t), MAX_ATTEMPTS, RETRY_DELAY_MS));
    }

    public void sendNotification(Notification notification) {
        executor.submit(() -> {
            try {
                gatewayFor(notification.getType()).send(notification);
            } catch (Exception e) {
                System.out.println("Exception while sending notification: " + e);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
        try {
            // Wait for in-flight sends to finish, then force-stop anything stuck.
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
