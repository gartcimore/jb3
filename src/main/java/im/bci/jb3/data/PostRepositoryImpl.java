package im.bci.jb3.data;

import im.bci.jb3.frontend.PostSearchRQ;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Post> findPosts(DateTime start, DateTime end) {
        Query query = new Query().addCriteria(Criteria.where("time").gte(start.toDate()).lt(end.toDate())).with(new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        List<Post> result = mongoTemplate.find(query, Post.class, COLLECTION_NAME);
        return result;
    }

    @Override
    public void save(Post post) {
        mongoTemplate.save(post, COLLECTION_NAME);
    }

    @Override
    public Post findOne(String id) {
        return mongoTemplate.findById(id, Post.class, COLLECTION_NAME);
    }

    private static final String COLLECTION_NAME = "post";

    @Override
    public Post findOne(DateTime start, DateTime end) {
        Query query = new Query().addCriteria(Criteria.where("time").gte(start.toDate()).lt(end.toDate())).with(new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        return mongoTemplate.findOne(query, Post.class, COLLECTION_NAME);
    }

    @Override
    public List<Post> search(PostSearchRQ fo) {
        Query query = new Query().addCriteria(Criteria.where("time").gte(fo.getFrom().toDate()).lt(fo.getTo().toDate()));
        if (StringUtils.isNotBlank(fo.getMessageFilter())) {
            query = query.addCriteria(Criteria.where("message").regex(fo.getMessageFilter()));
        }
        if (StringUtils.isNotBlank(fo.getNicknameFilter())) {
            query = query.addCriteria(Criteria.where("nickname").regex(fo.getNicknameFilter()));
        }
        query = query.with(new PageRequest(fo.getPage(), fo.getPageSize(), Sort.Direction.DESC, "time"));
        return mongoTemplate.find(query, Post.class, COLLECTION_NAME);
    }

}
