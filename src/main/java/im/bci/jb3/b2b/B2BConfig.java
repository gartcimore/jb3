package im.bci.jb3.b2b;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
@ConfigurationProperties("jb3.b2b")
public class B2BConfig implements BeanFactoryAware {

    private Map<String, B2BPeer> peers = new HashMap<>();
    private ConfigurableBeanFactory beanFactory;

    @PostConstruct
    public void setup() {
        for (B2BPeer peer : peers.values()) {
            for (B2BPeerRoom room : peer.getRooms()) {
                B2BGateway gateway = new B2BGateway(peer, room);
                beanFactory.registerSingleton(room.getLocalName() + "B2BGateway", gateway);
            }
        }
    }

    public Map<String, B2BPeer> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, B2BPeer> peers) {
        this.peers = peers;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

}
