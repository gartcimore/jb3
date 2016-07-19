package im.bci.jb3.bouchot.data;

import java.util.List;
import org.joda.time.DateTime;

import im.bci.jb3.coincoin.PostSearchRQ;

public interface PostRepository {
    
    List<Post> findPosts(DateTime start, DateTime end, String room);
    
    Post findOne(String room, DateTime start, DateTime end);

    void save(Post post);

    Post findOne(String id);
    
    boolean existsByGatewayPostId(GatewayPostId gpid);
    
    List<Post> search(PostSearchRQ fo);
    
    void deleteOldPosts();

}
