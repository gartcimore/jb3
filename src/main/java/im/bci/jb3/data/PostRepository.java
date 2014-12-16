package im.bci.jb3.data;

import java.util.List;
import org.joda.time.DateTime;

public interface PostRepository {
    
    List<Post> findPosts(DateTime start, DateTime end);
    
    Post findOne(DateTime start, DateTime end);

    public void save(Post post);

    public Post findOne(String id);

}
