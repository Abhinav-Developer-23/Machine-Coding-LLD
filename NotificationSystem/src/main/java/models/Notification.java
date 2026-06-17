package models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Notification {
    private final String id;
    private final Recipient recipient;
    private final NotificationType type;
    private final String message;
    private final String subject; // Optional, for email

    private Notification(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.recipient = builder.recipient;
        this.type = builder.type;
        this.message = builder.message;
        this.subject = builder.subject;
    }

    // Builder Class
    public static class Builder {
        private final Recipient recipient;
        private final NotificationType type;
        private String message;
        private String subject = "";

        public Builder(Recipient recipient, NotificationType type) {
            this.recipient = recipient;
            this.type = type;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Notification build() {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Notification message body is required.");
            }
            return new Notification(this);
        }
    }
}
