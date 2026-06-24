package stackoverflow.entities;

import lombok.Getter;
import stackoverflow.enums.EventType;

@Getter
public class Event {
  private final EventType type;
  private final User actor;
  private final Post targetPost;
  private final boolean reversal;

  public Event(EventType type, User actor, Post targetPost) {
    this(type, actor, targetPost, false);
  }

  public Event(EventType type, User actor, Post targetPost, boolean reversal) {
    this.type = type;
    this.actor = actor;
    this.targetPost = targetPost;
    this.reversal = reversal;
  }
}
