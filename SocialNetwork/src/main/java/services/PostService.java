package services;

import entities.Comment;
import entities.Post;
import entities.User;
import observer.PostObserver;
import repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostService {
    private final PostRepository postRepository;
    private final List<PostObserver> observers = new ArrayList<>();

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void addObserver(PostObserver observer) { observers.add(observer); }

    public Post createPost(User author, String content) {
        Post post = new Post(author, content);
        postRepository.save(post);
        author.addPost(post);
        observers.forEach(observer -> observer.onPostCreated(post)); // Notify observers
        return post;
    }

    public void likePost(User user, String postId) {
        Post post = postRepository.findById(postId);
        if (post.getLikes().contains(user)) {
            return; // already liked nothing to record or notify
        }
        post.addLike(user);
        observers.forEach(observer -> observer.onLike(post, user));
    }

    public void addComment(User author, String commentableId, String content) {
        Comment comment = new Comment(author, content);
        Post post = postRepository.findById(commentableId);
        post.addComment(comment);
        observers.forEach(observer -> observer.onComment(post, comment));
    }
}
