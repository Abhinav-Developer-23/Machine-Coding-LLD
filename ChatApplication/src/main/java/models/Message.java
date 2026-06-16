package models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public final class Message {
    private final String id;
    private final User sender;
    private final String content;
    private final LocalDateTime timestamp;

    public Message(User sender, String content) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", sender.getName(), content);
    }
}
