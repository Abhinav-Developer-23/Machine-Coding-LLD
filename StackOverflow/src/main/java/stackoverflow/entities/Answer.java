package stackoverflow.entities;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Answer extends Post {
  @Setter private boolean isAccepted = false;

  public Answer(String body, User author) {
    super(UUID.randomUUID().toString(), body, author);
  }
}
