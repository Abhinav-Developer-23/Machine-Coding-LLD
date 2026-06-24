package stackoverflow.entities;

import java.util.UUID;
import lombok.Getter;

@Getter
public class User {
  private final String id;
  private final String name;
  private final java.util.concurrent.atomic.AtomicInteger reputation;

  public User(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.reputation = new java.util.concurrent.atomic.AtomicInteger(0);
  }

  public void updateReputation(int change) {
    this.reputation.addAndGet(change);
  }

  public int getReputation() {
    return reputation.get();
  }
}
