package im.bci.jb3.websocket.messages.s2c;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class NorlogeS2C {
    private String messageId;
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private DateTime time;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }
}
