package stackoverflow.entities;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class Content {
  protected final String id;
  protected final String body;
  protected final User author;
  protected final LocalDateTime creationTime;

  protected Content(String id, String body, User author) {
    this.id = id;
    this.body = body;
    this.author = author;
    this.creationTime = LocalDateTime.now();
  }
}
