package im.bci.jb3.bouchot.data;

import java.util.List;
import org.joda.time.DateTime;

import im.bci.jb3.coincoin.PostSearchRQ;

public interface PostRepository {

    List<Post> findPosts(DateTime start, DateTime end, String room);

    List<Post> findPostsReverse(DateTime start, DateTime end, String room);

    long countPosts(DateTime start, DateTime end, String room);

    Post findOne(String room, DateTime start, DateTime end, int indice);

    void save(Post post);

    Post findOne(String id);

    boolean existsById(String gpid);

    Post findOneByGatewayId(GatewayPostId gpid);

    boolean existsByGatewayPostId(GatewayPostId gpid);

    List<Post> search(PostSearchRQ rq);
}
