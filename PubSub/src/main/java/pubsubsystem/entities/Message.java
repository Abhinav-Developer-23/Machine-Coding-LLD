package pubsubsystem.entities;

import lombok.Getter;

import java.time.Instant;

public class Message {
    @Getter
    private final String payload;
    private final Instant timestamp;

    public Message(String payload) {
        this.payload = payload;
        this.timestamp = Instant.now();
    }

    @Override
    public String toString() {
        return "Message{" + "payload='" + payload + '\'' + '}';
    }
}
