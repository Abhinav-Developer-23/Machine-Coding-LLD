package models;

import java.util.List;

public class OneToOneChat extends Chat {

    public OneToOneChat(User user1, User user2) {
        super();
        this.members.addAll(List.of(user1, user2));
    }

    @Override
    public String getName(User perspectiveUser) {
        // The chat name from a user's perspective is the other user's name.
        return members.stream()
                .filter(member -> !member.equals(perspectiveUser))
                .findFirst()
                .map(User::getName)
                .orElse("Unknown Chat");
    }
}
