package im.bci.jb3.data;

import im.bci.jb3.frontend.PostSearchRQ;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class FortuneRepositoryImpl implements FortuneRepository {

    private static final String COLLECTION_NAME = "fortune";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Fortune save(Fortune f) {
        mongoTemplate.save(f, COLLECTION_NAME);
        return f;
    }

    @Override
    public Fortune findOne(String fortuneId) {
        return mongoTemplate.findById(fortuneId, Fortune.class, COLLECTION_NAME);
    }

    @Override
    public List<Fortune> search(PostSearchRQ rq) {
        Query query = new Query().addCriteria(Criteria.where("time").gte(new Date(rq.getFrom())).lt(new Date(rq.getTo())));
        if (StringUtils.isNotBlank(rq.getMessageFilter())) {
            query = query.addCriteria(Criteria.where("posts").elemMatch(Criteria.where("message").regex(rq.getMessageFilter())));
        }
        if (StringUtils.isNotBlank(rq.getNicknameFilter())) {
            query = query.addCriteria(Criteria.where("posts").elemMatch(Criteria.where("nickname").regex(rq.getNicknameFilter())));
        }
        query = query.with(new PageRequest(rq.getPage(), rq.getPageSize(), Sort.Direction.DESC, "time"));
        return mongoTemplate.find(query, Fortune.class, COLLECTION_NAME);
    }

}
