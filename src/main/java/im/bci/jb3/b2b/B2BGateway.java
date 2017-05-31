package im.bci.jb3.b2b;

import im.bci.jb3.bouchot.gateway.AbstractTsvBouchotGateway;
import im.bci.jb3.bouchot.gateway.BouchotConfig;

/**
 *
 * @author devnewton
 */
public class B2BGateway extends AbstractTsvBouchotGateway{

    public B2BGateway(B2BPeer peer, B2BPeerRoom room) {
        super(createConfig(peer, room));
    }
    
    static private BouchotConfig createConfig(B2BPeer peer, B2BPeerRoom room) {
        BouchotConfig config = new BouchotConfig();
        config.setRoom(room.getLocalName());
        config.setGetUrl(peer.getUrl() + "/legacy/tsv?room=" + room.getRemoteName());
        config.setPostUrl(peer.getUrl() + "/legacy/post?room=" + room.getRemoteName());
        config.setLastIdParameterName("last");
        config.setMessageContentParameterName("message");
        return config;
    }
    
}
