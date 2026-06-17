package services;

import entities.Post;
import entities.User;
import lombok.Setter;
import strategy.ChronologicalStrategy;
import strategy.NewsFeedGenerationStrategy;

import java.util.List;

@Setter
public class NewsFeedService {
    private NewsFeedGenerationStrategy strategy;

    public NewsFeedService() {
        this.strategy = new ChronologicalStrategy(); // Default strategy
    }

    public List<Post> getNewsFeed(User user) {
        return strategy.generateFeed(user);
    }
}
