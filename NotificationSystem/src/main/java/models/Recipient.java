package models;

import lombok.Getter;

import java.util.Optional;

@Getter
public class Recipient {
    private final String userId;
    private final Optional<String> email;
    private final Optional<String> phoneNumber;
    private final Optional<String> pushToken;

    public Recipient(String userId, String email, String phoneNumber, String pushToken) {
        this.userId = userId;
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.pushToken = Optional.ofNullable(pushToken);
    }
}
