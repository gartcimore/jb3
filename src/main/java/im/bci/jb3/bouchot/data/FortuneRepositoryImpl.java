package im.bci.jb3.bouchot.data;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import im.bci.jb3.coincoin.FortuneSearchRQ;

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
    public List<Fortune> search(FortuneSearchRQ rq) {
        Query query = new Query();
        Interval interval = rq.getDateInterval();
        if(null != interval) {
        	query = query.addCriteria(Criteria.where("time").gte(interval.getStart().toDate()).lt(interval.getEnd().toDate()));
        }
        if (StringUtils.isNotBlank(rq.getMessageFilter())) {
            query = query.addCriteria(Criteria.where("posts").elemMatch(Criteria.where("message").regex(rq.getMessageFilter())));
        }
        if (StringUtils.isNotBlank(rq.getNicknameFilter())) {
            query = query.addCriteria(Criteria.where("posts").elemMatch(Criteria.where("nickname").regex(rq.getNicknameFilter())));
        }
        query = query.with(PageRequest.of(rq.getPage(), rq.getPageSize(), Sort.Direction.DESC, "time"));
        return mongoTemplate.find(query, Fortune.class, COLLECTION_NAME);
    }

}
