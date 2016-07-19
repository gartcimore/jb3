package im.bci.jb3.bouchot.gateway;

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
        conf.setGetUrl("https://faab.euromussels.eu/data/backend.xml");
        conf.setPostUrl("https://faab.euromussels.eu/add.php");
        conf.setTagsEncoded(false);
        conf.setMessageContentParameterName("message");
        conf.setUsingCrapCertificate(true);
        return conf;
    }

    public EuromusselsGateway() {
        super(createConf());
    }

}
