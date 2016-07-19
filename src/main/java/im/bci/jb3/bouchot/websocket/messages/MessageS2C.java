package im.bci.jb3.bouchot.websocket.messages;

import java.util.List;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.websocket.messages.s2c.NorlogeS2C;
import im.bci.jb3.bouchot.websocket.messages.s2c.PresenceS2C;

public class MessageS2C {

    private List<Post> posts;
    private PresenceS2C presence;
    private String ack;
    private NorlogeS2C norloge;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public PresenceS2C getPresence() {
        return presence;
    }

    public void setPresence(PresenceS2C presence) {
        this.presence = presence;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public NorlogeS2C getNorloge() {
        return norloge;
    }

    public void setNorloge(NorlogeS2C norloge) {
        this.norloge = norloge;
    }

}
