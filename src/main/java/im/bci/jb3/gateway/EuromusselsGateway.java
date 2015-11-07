package im.bci.jb3.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class EuromusselsGateway extends AbstractBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("euromussels");
        conf.setGetUrl("https://euromussels.eu/?q=tribune.xml");
        conf.setPostUrl("https://euromussels.eu/?q=tribune/post");
        conf.setLastIdParameterName("last_id");
        conf.setMessageContentParameterName("message");
        conf.setUsingCrapCertificate(true);
        return conf;
    }

    public EuromusselsGateway() {
        super(createConf());
    }

}
