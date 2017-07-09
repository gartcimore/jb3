package im.bci.jb3.bouchot.data;

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
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Component;

import im.bci.jb3.coincoin.PostSearchRQ;

@Component
public class PostRepositoryImpl implements PostRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value("${jb3.room.default}")
	private String defaultRoom;

	@Value("${jb3.room.history.size}")
	private int roomHistorySize;

	private String roomOrDefault(String room) {
		return StringUtils.isNotBlank(room) ? room : defaultRoom;
	}

	@Override
	public List<Post> findPosts(DateTime start, DateTime end, String room) {
		Criteria criteria = Criteria.where("time").gte(start.toDate()).lt(end.toDate()).and("room")
				.is(roomOrDefault(room));
		Query query = new Query().addCriteria(criteria)
				.with(new PageRequest(0, roomHistorySize, Sort.Direction.DESC, "time", "gatewayPostId.postId"));
		List<Post> result = mongoTemplate.find(query, Post.class, COLLECTION_NAME);
		return result;
	}

	@Override
	public List<Post> findPostsReverse(DateTime start, DateTime end, String room) {
		Criteria criteria = Criteria.where("time").gte(start.toDate()).lt(end.toDate()).and("room")
				.is(roomOrDefault(room));
		Query query = new Query().addCriteria(criteria)
				.with(new PageRequest(0, roomHistorySize, Sort.Direction.ASC, "time", "gatewayPostId.postId"));
		List<Post> result = mongoTemplate.find(query, Post.class, COLLECTION_NAME);
		return result;
	}

	@Override
	public long countPosts(DateTime start, DateTime end, String room) {
		Criteria criteria = Criteria.where("time").gte(start.toDate()).lt(end.toDate()).and("room")
				.is(roomOrDefault(room));
		Query query = new Query().addCriteria(criteria)
				.with(new PageRequest(0, roomHistorySize, Sort.Direction.ASC, "time", "gatewayPostId.postId"));
		return mongoTemplate.count(query, COLLECTION_NAME);
	}

	@Override
	public void save(Post post) {
		post.setRoom(roomOrDefault(post.getRoom()));
		mongoTemplate.save(post, COLLECTION_NAME);
	}

	@Override
	public Post findOne(String id) {
		return mongoTemplate.findById(id, Post.class, COLLECTION_NAME);
	}

	@Override
	public boolean existsById(String id) {
		Query query = new Query().addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.exists(query, COLLECTION_NAME);
	}

	private static final String COLLECTION_NAME = "post";

	@Override
	public Post findOne(String room, DateTime start, DateTime end, int indice) {
		Criteria criteria = Criteria.where("time").gte(start.toDate()).lt(end.toDate()).and("room")
				.is(roomOrDefault(room));
		Query query = new Query().addCriteria(criteria)
				.with(new PageRequest(0, 9, Sort.Direction.ASC, "time", "gatewayPostId.postId"));
		List<Post> posts = mongoTemplate.find(query, Post.class, COLLECTION_NAME);
		if (posts.isEmpty()) {
			return null;
		}
		int clampedIndice = Math.min(posts.size(), Math.max(1, indice));
		return posts.get(clampedIndice - 1);
	}

	@Override
	public List<Post> search(PostSearchRQ rq) {
		Query query;
		if (StringUtils.isNotBlank(rq.getMessageFilter())) {
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(rq.getMessageFilter());
			query = TextQuery.queryText(criteria).sortByScore();
		} else {
			query = new Query();
		}
		if (StringUtils.isNotBlank(rq.getNicknameFilter())) {
			query = query.addCriteria(Criteria.where("nickname").is(rq.getNicknameFilter()));
		}
		if (StringUtils.isNotBlank(rq.getRoomFilter())) {
			query = query.addCriteria(Criteria.where("room").is(rq.getRoomFilter()));
		}
		DateTime sinceDate = rq.getSinceDate();
		DateTime untilDate = rq.getUntilDate();
		if (null != sinceDate && null != untilDate) {
			query.addCriteria(Criteria.where("time").gte(sinceDate.toDate()).lte(untilDate.toDate()));
		} else {
			if (null != sinceDate) {
				query.addCriteria(Criteria.where("time").gte(sinceDate.toDate()));
			}
			if (null != untilDate) {
				query.addCriteria(Criteria.where("time").lte(untilDate.toDate()));
			}
		}
		query.with(new PageRequest(rq.getPage(), rq.getPageSize(), Sort.Direction.ASC, "time"));
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
		Query roomQuery = new Query().addCriteria(
				Criteria.where("room").ne(defaultRoom).and("time").lt(DateTime.now().minus(roomPostsTTL).toDate()));
		mongoTemplate.remove(roomQuery, Post.class, COLLECTION_NAME);

		Query query = new Query().addCriteria(
				Criteria.where("room").is(defaultRoom).and("time").lt(DateTime.now().minus(postsTTL).toDate()));
		mongoTemplate.remove(query, Post.class, COLLECTION_NAME);
	}

	@Override
	public boolean existsByGatewayPostId(GatewayPostId gpid) {
		Query query = new Query().addCriteria(Criteria.where("gatewayPostId.gateway").is(gpid.getGateway())
				.and("gatewayPostId.postId").is(gpid.getPostId()));
		return mongoTemplate.exists(query, COLLECTION_NAME);
	}

	@Override
	public Post findOneByGatewayId(GatewayPostId gpid) {
		Query query = new Query().addCriteria(Criteria.where("gatewayPostId.gateway").is(gpid.getGateway())
				.and("gatewayPostId.postId").is(gpid.getPostId()));
		return mongoTemplate.findOne(query, Post.class, COLLECTION_NAME);
	}

}
