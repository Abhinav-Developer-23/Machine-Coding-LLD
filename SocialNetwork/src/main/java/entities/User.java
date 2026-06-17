package entities;

import lombok.Getter;

import java.util.*;

@Getter
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final Set<User> friends = new HashSet<>();
    private final List<Post> posts = new ArrayList<>();

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public void addPost(Post post) {
        posts.add(post);
    }
}
