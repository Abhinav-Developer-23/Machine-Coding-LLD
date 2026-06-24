package stackoverflow.strategy;

import java.util.List;
import java.util.stream.Collectors;
import stackoverflow.entities.Question;
import stackoverflow.entities.Tag;

public class TagSearchStrategy implements SearchStrategy {
  private final Tag tag;

  public TagSearchStrategy(Tag tag) {
    this.tag = tag;
  }

  @Override
  public List<Question> filter(List<Question> questions) {
    return questions.stream()
        .filter(
            q ->
                q.getTags().stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName())))
        .collect(Collectors.toList());
  }
}
