package stackoverflow.observer;

import stackoverflow.entities.Event;
import stackoverflow.entities.User;
import stackoverflow.enums.EventType;

public class ReputationManager implements PostObserver {
  private static final int QUESTION_UPVOTE_REP = 5;
  private static final int ANSWER_UPVOTE_REP = 10;
  private static final int ACCEPTED_ANSWER_REP = 15;
  private static final int AUTHOR_DOWNVOTE_PENALTY = -2;
  private static final int VOTER_DOWNVOTE_PENALTY = -1;

  @Override
  public void onPostEvent(Event event) {
    int sign = event.isReversal() ? -1 : 1;
    User postAuthor = event.getTargetPost().getAuthor();
    switch (event.getType()) {
      case UPVOTE_QUESTION:
        postAuthor.updateReputation(sign * QUESTION_UPVOTE_REP);
        break;
      case DOWNVOTE_QUESTION:
        postAuthor.updateReputation(sign * AUTHOR_DOWNVOTE_PENALTY);
        event.getActor().updateReputation(sign * VOTER_DOWNVOTE_PENALTY);
        break;
      case UPVOTE_ANSWER:
        postAuthor.updateReputation(sign * ANSWER_UPVOTE_REP);
        break;
      case DOWNVOTE_ANSWER:
        postAuthor.updateReputation(sign * AUTHOR_DOWNVOTE_PENALTY);
        event.getActor().updateReputation(sign * VOTER_DOWNVOTE_PENALTY);
        break;
      case ACCEPT_ANSWER:
        postAuthor.updateReputation(sign * ACCEPTED_ANSWER_REP);
        break;
    }
  }
}
