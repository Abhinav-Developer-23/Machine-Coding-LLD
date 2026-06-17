package strategy;

import entities.Post;
import entities.User;

import java.util.List;

public interface NewsFeedGenerationStrategy {
    List<Post> generateFeed(User user);
}
