package im.bci.jb3.bouchot.gateway;

/**
 *
 * @author devnewton
 */
public class Jb3BouchotConfig {
    private String localRoom;
    private String remoteRoom;
    private String webdirectcoinURL;

    public String getLocalRoom() {
        return localRoom;
    }

    public void setLocalRoom(String localRoom) {
        this.localRoom = localRoom;
    }

    public String getRemoteRoom() {
        return remoteRoom;
    }

    public void setRemoteRoom(String remoteRoom) {
        this.remoteRoom = remoteRoom;
    }

    public String getWebdirectcoinURL() {
        return webdirectcoinURL;
    }

    public void setWebdirectcoinURL(String webdirectcoinURL) {
        this.webdirectcoinURL = webdirectcoinURL;
    }
}
