package im.bci.jb3.b2b;

import org.joda.time.DateTime;

/**
 *
 * @author devnewton
 */
public class B2BPeerRoom {

    private String remoteName;
    private String localName;
    private DateTime lastPull = DateTime.now().minusDays(1);

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public DateTime getLastPull() {
        return lastPull;
    }

    public void setLastPull(DateTime lastPull) {
        this.lastPull = lastPull;
    }

}
