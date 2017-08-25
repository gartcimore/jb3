package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class TaabGateway extends AbstractLegacyBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("taab");
        conf.setGetUrl("https://taab.bci.im/get.php");
        conf.setPostUrl("https://taab.bci.im/post.php");
        conf.setMessageContentParameterName("message");
        conf.setTagsEncoded(false);
        conf.setUsingXPost(true);
        conf.setLastIdParameterName("lastId");
        return conf;
    }

    public TaabGateway() {
        super(createConf());
    }

}
