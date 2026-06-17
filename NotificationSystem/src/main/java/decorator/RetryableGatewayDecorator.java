package decorator;

import gateway.DeliveryException;
import gateway.NotificationGateway;
import models.Notification;

public class RetryableGatewayDecorator implements NotificationGateway {
    private final NotificationGateway wrappedGateway;
    private final int maxAttempts;
    private final long retryDelayMillis;

    public RetryableGatewayDecorator(NotificationGateway wrappedGateway, int maxAttempts, long retryDelayMillis) {
        this.wrappedGateway = wrappedGateway;
        this.maxAttempts = maxAttempts;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public void send(Notification notification) throws DeliveryException {
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                wrappedGateway.send(notification);
                return; // Success
            } catch (IllegalArgumentException e) {
                // Permanent failure such as missing contact info. Retrying cannot help.
                throw e;
            } catch (Exception e) {
                attempt++;
                System.out.println("Error: Attempt " + attempt + " failed for notification " + notification.getId() + ". Retrying...");
                if (attempt >= maxAttempts) {
                    System.out.println(e.getMessage());
                    throw new DeliveryException("Failed to send notification after " + maxAttempts + " attempts.", e);
                }
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new DeliveryException("Interrupted during retry backoff.", ie);
                }
            }
        }
    }
}
