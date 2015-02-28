package im.bci.jb3.data;

import im.bci.jb3.frontend.PostSearchRQ;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public List<Post> findPosts(DateTime start, DateTime end, String room) {
        Criteria criteria = Criteria.where("time").gte(start.toDate()).lt(end.toDate());
        if (StringUtils.isNotBlank(room)) {
            criteria = criteria.and("room").is(room);
        } else {
            criteria = criteria.andOperator(Criteria.where("room").exists(false).orOperator(Criteria.where("room").is(null)));
        }
        Query query = new Query().addCriteria(criteria).with(new PageRequest(0, 1000, Sort.Direction.DESC, "time"));

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
    public List<Post> search(PostSearchRQ rq) {
        Query query = new Query().addCriteria(Criteria.where("time").gte(new Date(rq.getFrom())).lt(new Date(rq.getTo())));
        if (StringUtils.isNotBlank(rq.getMessageFilter())) {
            query = query.addCriteria(Criteria.where("message").regex(rq.getMessageFilter()));
        }
        if (StringUtils.isNotBlank(rq.getNicknameFilter())) {
            query = query.addCriteria(Criteria.where("nickname").regex(rq.getNicknameFilter()));
        }
        query = query.with(new PageRequest(rq.getPage(), rq.getPageSize(), Sort.Direction.DESC, "time"));
        return mongoTemplate.find(query, Post.class, COLLECTION_NAME);
    }

    private Period postsTTL;
    
    @Value("${jb3.posts.ttl}")
    public void setPostsTTL(String ttl) {
        postsTTL = ISOPeriodFormat.standard().parsePeriod(ttl);
    }

    private Period roomPostsTTL;

    @Value("${jb3.room.posts.ttl}")
    public void setRoomPostsTTL(String ttl) {
        roomPostsTTL = ISOPeriodFormat.standard().parsePeriod(ttl);
    }

    @Override
    public void deleteOldPosts() {
        Query roomQuery = new Query().addCriteria(Criteria.where("room").ne(null).and("time").lt(DateTime.now().minus(roomPostsTTL).toDate()));
        mongoTemplate.remove(roomQuery, Post.class, COLLECTION_NAME);
        
        Query query = new Query().addCriteria(Criteria.where("room").is(null).and("time").lt(DateTime.now().minus(postsTTL).toDate()));
        mongoTemplate.remove(query, Post.class, COLLECTION_NAME);
    }

}
