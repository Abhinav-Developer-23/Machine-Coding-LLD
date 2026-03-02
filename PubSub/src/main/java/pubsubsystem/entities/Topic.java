package pubsubsystem.entities;

import lombok.Getter;
import pubsubsystem.subscriber.Subscriber;

import java.util.Set;
import java.util.concurrent.*;

public class Topic {
    @Getter
    private final String name;
    private final Set<Subscriber> subscribers;
    private final ExecutorService deliveryExecutor;

    public Topic(String name) {
        this.name = name;
        this.subscribers = new CopyOnWriteArraySet<>();
        this.deliveryExecutor = new ThreadPoolExecutor(
                4, 16, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void broadcast(Message message) {
        for (Subscriber subscriber : subscribers) {
            // Each delivery runs on a dedicated thread from the pool, keeping the
            // publisher's thread non-blocking. The try/catch isolates failures so
            // one bad subscriber cannot affect others.
            CompletableFuture.runAsync(() -> {
                try {
                    subscriber.onMessage(message);
                } catch (Exception e) {
                    System.err.println("Error delivering message to subscriber "
                            + subscriber.getId() + ": " + e.getMessage());
                }
            }, deliveryExecutor);
        }
    }
}
