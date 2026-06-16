package models;

import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

@Getter
public class User {
    private final String id;
    private final String name;

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public void onMessageReceived(Message message, Chat chatContext) {
        System.out.printf("[Notification for %s in chat '%s'] %s: %s\n",
                this.getName(), chatContext.getName(this), message.getSender().getName(), message.getContent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
