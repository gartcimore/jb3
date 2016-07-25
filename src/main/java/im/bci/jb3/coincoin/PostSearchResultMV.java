package im.bci.jb3.coincoin;

import java.util.Collections;
import java.util.List;

import im.bci.jb3.bouchot.data.Post;

public class PostSearchResultMV {

    private List<Post> posts = Collections.emptyList();
    private boolean hasNext;
    private boolean hasPrevious;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

}
