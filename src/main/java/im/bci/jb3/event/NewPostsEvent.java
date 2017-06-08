package im.bci.jb3.event;

import im.bci.jb3.bouchot.data.Post;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author devnewton
 */
public class NewPostsEvent {

    private final List<Post> posts;

    public NewPostsEvent(Post post) {
        this.posts = Arrays.asList(post);
    }

    public NewPostsEvent(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

}
