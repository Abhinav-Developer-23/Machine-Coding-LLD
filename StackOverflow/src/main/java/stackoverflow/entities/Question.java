package stackoverflow.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import stackoverflow.enums.EventType;

@Getter
public class Question extends Post {
  private final String title;
  private final Set<Tag> tags;
  private final List<Answer> answers = new ArrayList<>();
  private Answer acceptedAnswer;

  public Question(String title, String body, User author, Set<Tag> tags) {
    super(UUID.randomUUID().toString(), body, author);
    this.title = title;
    this.tags = tags;
  }

  public void addAnswer(Answer answer) {
    this.answers.add(answer);
  }

  public synchronized void acceptAnswer(Answer answer) {
    if (this.acceptedAnswer == null && this.answers.contains(answer)) {
      this.acceptedAnswer = answer;
      answer.setAccepted(true);
      notifyObservers(new Event(EventType.ACCEPT_ANSWER, answer.getAuthor(), answer));
    }
  }
}
