package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class EuromusselsGateway extends AbstractLegacyBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("euromussels");
        conf.setBackendFormat(BouchotBackendFormat.XML);
        conf.setGetUrl("http://faab.euromussels.eu/data/backend.xml");
        conf.setPostUrl("http://faab.euromussels.eu/add.php");
        conf.setTagsEncoded(false);
        conf.setMessageContentParameterName("message");
        return conf;
    }

    public EuromusselsGateway() {
        super(createConf());
    }

}
