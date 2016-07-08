package im.bci.jb3.websocket.messages;

import im.bci.jb3.websocket.Presence;

/**
 *
 * @author devnewton
 */
public class PresenceMsg {

    private String mouleId;
    private Presence presence;

    public String getMouleId() {
        return mouleId;
    }

    public void setMouleId(String mouleId) {
        this.mouleId = mouleId;
    }

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

}
