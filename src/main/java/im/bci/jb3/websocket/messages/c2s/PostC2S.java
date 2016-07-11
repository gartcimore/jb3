package im.bci.jb3.websocket.messages.c2s;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostC2S {

    private String nickname;
    private String message;
    private String room;
    private String auth;

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

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
