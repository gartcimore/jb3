package im.bci.jb3.b2b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author devnewton
 */
public class B2BPeer {

    private String url;
    private List<B2BPeerRoom> rooms = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<B2BPeerRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<B2BPeerRoom> rooms) {
        this.rooms = rooms;
    }

}
