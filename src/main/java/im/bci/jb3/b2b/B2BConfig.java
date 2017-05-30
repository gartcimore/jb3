package im.bci.jb3.b2b;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
@ConfigurationProperties("jb3.b2b")
public class B2BConfig {
     private Map<String, B2BPeer> peers = new HashMap<>();

    public Map<String, B2BPeer> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, B2BPeer> peers) {
        this.peers = peers;
    }
     
     
     

}
