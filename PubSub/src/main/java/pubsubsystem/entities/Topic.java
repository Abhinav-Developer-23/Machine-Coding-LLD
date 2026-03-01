package pubsubsystem.entities;

import lombok.Getter;
import pubsubsystem.subscriber.Subscriber;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class Topic {
    @Getter
    private final String name;
    private final Set<Subscriber> subscribers;
    private final ExecutorService deliveryExecutor;

    public Topic(String name, ExecutorService deliveryExecutor) {
        this.name = name;
        this.deliveryExecutor = deliveryExecutor;
        this.subscribers = new CopyOnWriteArraySet<>();
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void broadcast(Message message) {
        for (Subscriber subscriber : subscribers) {
            // We submit each delivery as a separate task to the shared ExecutorService instead of
            // calling subscriber.onMessage() directly on the publisher's thread. This achieves two things:
            //
            // 1. Non-blocking publish: The publisher's thread returns immediately after submitting tasks.
            //    It doesn't wait for any subscriber to finish processing — a slow or stuck subscriber
            //    cannot block or delay the publisher (or other subscribers).
            //
            // 2. Isolated failure: Each delivery runs in its own task. If one subscriber throws an
            //    exception, the try/catch here handles it without affecting the other deliveries.
            deliveryExecutor.submit(() -> {
                try {
                    subscriber.onMessage(message);
                } catch (Exception e) {
                    System.err.println("Error delivering message to subscriber " + subscriber.getId() + ": " + e.getMessage());
                }
            });
        }
    }
}
