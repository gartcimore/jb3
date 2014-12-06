package im.bci.jb3.data;

import java.util.List;
import org.springframework.data.annotation.Id;

public class Fortune {

    @Id
    private String id;

    private List<Post> posts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}
