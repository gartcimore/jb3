package im.bci.jb3.data;

import im.bci.jb3.frontend.PostSearchRQ;
import java.util.List;
import org.joda.time.DateTime;

public interface PostRepository {
    
    List<Post> findPosts(DateTime start, DateTime end, String room);
    
    Post findOne(String room, DateTime start, DateTime end);

    void save(Post post);

    Post findOne(String id);
    
    boolean existsByGatewayPostId(GatewayPostId gpid);
    
    List<Post> search(PostSearchRQ fo);
    
    void deleteOldPosts();

}
