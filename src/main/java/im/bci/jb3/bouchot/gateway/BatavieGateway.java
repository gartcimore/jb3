package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class BatavieGateway extends AbstractXmlBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("batavie");
        conf.setGetUrl("https://batavie.leguyader.eu/remote.xml");
        conf.setPostUrl("https://batavie.leguyader.eu/index.php/add");
        conf.setMessageContentParameterName("message");
        conf.setTagsEncoded(false);
        conf.setUsingCrapCertificate(true);
        return conf;
    }

    public BatavieGateway() {
        super(createConf());
    }

}
