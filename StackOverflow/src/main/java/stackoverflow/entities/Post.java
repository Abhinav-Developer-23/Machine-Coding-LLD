package stackoverflow.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import stackoverflow.enums.EventType;
import stackoverflow.enums.VoteType;
import stackoverflow.observer.PostObserver;

public abstract class Post extends Content {
  private int voteCount = 0;
  private final Map<String, VoteType> voters = new HashMap<>();
  private final List<Comment> comments = new ArrayList<>();
  private final List<PostObserver> observers = new ArrayList<>();

  protected Post(String id, String body, User author) {
    super(id, body, author);
  }

  public void addObserver(PostObserver observer) {
    this.observers.add(observer);
  }

  protected void notifyObservers(Event event) {
    observers.forEach(o -> o.onPostEvent(event));
  }

  public void addComment(Comment comment) {
    this.comments.add(comment);
  }

  public List<Comment> getComments() {
    return comments;
  }

  public synchronized void vote(User user, VoteType voteType) {
    String userId = user.getId();
    if (userId.equals(author.getId())) {
      return;
    }

    VoteType existingVote = voters.get(userId);
    if (existingVote == voteType) {
      return;
    }

    if (existingVote != null) {
      voteCount += existingVote == VoteType.UPVOTE ? -1 : 1;
      notifyObservers(new Event(eventTypeFor(existingVote), user, this, true));
    }

    voteCount += voteType == VoteType.UPVOTE ? 1 : -1;
    voters.put(userId, voteType);
    notifyObservers(new Event(eventTypeFor(voteType), user, this, false));
  }

  private EventType eventTypeFor(VoteType voteType) {
    if (this instanceof Question) {
      return voteType == VoteType.UPVOTE
          ? EventType.UPVOTE_QUESTION
          : EventType.DOWNVOTE_QUESTION;
    }
    return voteType == VoteType.UPVOTE ? EventType.UPVOTE_ANSWER : EventType.DOWNVOTE_ANSWER;
  }
}
