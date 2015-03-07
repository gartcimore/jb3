package im.bci.jb3.data;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class Post {

    @Id
    private String id;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private DateTime time;
    private String nickname;
    private String message;
    private String room;
    private GatewayPostId gatewayPostId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setGatewayPostId(GatewayPostId gatewayPostId) {
        this.gatewayPostId = gatewayPostId;
    }

    public GatewayPostId getGatewayPostId() {
        return gatewayPostId;
    }

}
