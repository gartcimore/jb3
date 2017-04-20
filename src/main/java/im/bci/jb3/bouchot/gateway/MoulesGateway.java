package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class MoulesGateway extends AbstractXmlBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("moules");
        conf.setGetUrl("http://moules.org/board/backend/xml");
        conf.setPostUrl("http://moules.org/board/add.php?backend=xml");
        conf.setLastIdParameterName("id");
        conf.setMessageContentParameterName("message");
        conf.setTagsEncoded(false);
        conf.setUsingXPost(true);
        return conf;
    }

    public MoulesGateway() {
        super(createConf());
    }

}
