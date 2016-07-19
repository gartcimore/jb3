package im.bci.jb3.bouchot.websocket.messages.s2c;

import im.bci.jb3.bouchot.websocket.messages.data.Presence;

/**
 *
 * @author devnewton
 */
public class PresenceS2C {

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
