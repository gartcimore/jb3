package im.bci.jb3.bouchot.data;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscription")
public class Subscription {

    private static final int EXPIRES_AFTER_SECONDS = 24 * 3600;

    @Id
    private String callback;

    @Indexed(expireAfterSeconds = EXPIRES_AFTER_SECONDS)
    private DateTime subscribedAt;

    private String room;
    private String bot;

    @Transient
    public DateTime getExpiresAt() {
        return subscribedAt.plusSeconds(EXPIRES_AFTER_SECONDS);
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public DateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(DateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBot() {
        return bot;
    }

    public void setBot(String bot) {
        this.bot = bot;
    }

}
