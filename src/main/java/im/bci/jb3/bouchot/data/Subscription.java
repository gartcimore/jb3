package im.bci.jb3.bouchot.data;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.data.mongodb.core.index.Indexed;

public class Subscription {
    
    private static final int EXPIRES_AFTER_SECONDS = 24 * 3600;
    
    @Indexed(unique = true)
    private String callback;
    
    @Indexed(expireAfterSeconds = EXPIRES_AFTER_SECONDS)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private DateTime subscribedAt;
    
    private String room;
    
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
    
}
