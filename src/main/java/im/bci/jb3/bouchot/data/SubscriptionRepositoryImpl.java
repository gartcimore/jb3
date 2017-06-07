package im.bci.jb3.bouchot.data;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${jb3.room.default}")
    private String defaultRoom;

    private static final String COLLECTION_NAME = "subscription";

    private String roomOrDefault(String room) {
        return StringUtils.isNotBlank(room) ? room : defaultRoom;
    }

    @Override
    public void save(Subscription subscription) {
        subscription.setSubscribedAt(DateTime.now());
        subscription.setRoom(roomOrDefault(subscription.getRoom()));
        mongoTemplate.save(subscription, COLLECTION_NAME);
    }
}
