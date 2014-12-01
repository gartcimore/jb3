package im.bci.jb3.data;

import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PostRepository extends MongoRepository<Post, String> {
    
    @Query("{ 'time' : {$gte: ?0, $lt: ?1} }")
    Page<Post> findPosts(Date start, Date end, Pageable pageable);

}
