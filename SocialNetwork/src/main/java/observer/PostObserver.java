package observer;

import entities.Comment;
import entities.Post;
import entities.User;

public interface PostObserver {
    void onPostCreated(Post post);
    void onLike(Post post, User user);
    void onComment(Post post, Comment comment);
}
