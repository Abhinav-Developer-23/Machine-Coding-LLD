package stackoverflow;

import java.util.List;
import java.util.Set;
import stackoverflow.entities.Answer;
import stackoverflow.entities.Question;
import stackoverflow.entities.Tag;
import stackoverflow.entities.User;
import stackoverflow.enums.VoteType;
import stackoverflow.strategy.SearchStrategy;
import stackoverflow.strategy.TagSearchStrategy;
import stackoverflow.strategy.UserSearchStrategy;

public class StackOverflowDemo {
  public static void main(String[] args) {
    StackOverflowService service = new StackOverflowService();

    User alice = service.createUser("Alice");
    User bob = service.createUser("Bob");
    User charlie = service.createUser("Charlie");

    System.out.println("--- Alice posts a question ---");
    Tag javaTag = new Tag("java");
    Tag designPatternsTag = new Tag("design-patterns");
    Set<Tag> tags = Set.of(javaTag, designPatternsTag);
    Question question =
        service.postQuestion(
            alice.getId(),
            "How to implement Observer Pattern?",
            "Details about Observer Pattern...",
            tags);
    printReputations(alice, bob, charlie);

    System.out.println("\n--- Bob and Charlie post answers ---");
    Answer bobAnswer =
        service.postAnswer(
            question.getId(), "You can use the java.util.Observer interface.", bob.getId());
    Answer charlieAnswer =
        service.postAnswer(
            question.getId(),
            "A better way is to create your own Observer interface.",
            charlie.getId());
    printReputations(alice, bob, charlie);

    System.out.println("\n--- Voting Occurs ---");
    service.voteOnPost(question.getId(), bob.getId(), VoteType.UPVOTE);
    service.voteOnPost(charlieAnswer.getId(), bob.getId(), VoteType.UPVOTE);
    service.voteOnPost(bobAnswer.getId(), alice.getId(), VoteType.DOWNVOTE);
    printReputations(alice, bob, charlie);

    System.out.println("\n--- Comments ---");
    service.addComment(question.getId(), "Can you share what you have tried?", bob.getId());
    service.addComment(charlieAnswer.getId(), "Glad this worked for you!", alice.getId());
    System.out.println("Question has " + question.getComments().size() + " comment(s)");
    System.out.println("Accepted answer has " + charlieAnswer.getComments().size() + " comment(s)");

    System.out.println("\n--- Alice accepts Charlie's answer ---");
    service.acceptAnswer(question.getId(), charlieAnswer.getId());
    printReputations(alice, bob, charlie);

    System.out.println("\n--- (C) Combined Search: Questions by 'Alice' with tag 'java' ---");
    List<SearchStrategy> filtersC = List.of(new UserSearchStrategy(alice), new TagSearchStrategy(javaTag));
    List<Question> searchResults = service.searchQuestions(filtersC);
    searchResults.forEach(q -> System.out.println("  - Found: " + q.getTitle()));
  }

  private static void printReputations(User... users) {
    System.out.println("--- Current Reputations ---");
    for (User user : users) {
      System.out.printf("%s: %d%n", user.getName(), user.getReputation());
    }
  }
}
