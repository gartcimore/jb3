package im.bci.jb3.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
    private List<PostRevision> revisions;
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

    public List<PostRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<PostRevision> revisions) {
        this.revisions = revisions;
    }

    public void revise(String newMessage) {
        if (null == revisions) {
            revisions = new ArrayList<PostRevision>();
        }
        if (revisions.isEmpty()) {
            PostRevision revision = new PostRevision();
            revision.setMessage(message);
            revision.setTime(time);
            revisions.add(revision);
        }
        PostRevision revision = new PostRevision();
        revision.setMessage(newMessage);
        revision.setTime(DateTime.now(DateTimeZone.UTC));
        revisions.add(revision);
        this.message = newMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Post other = (Post) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
