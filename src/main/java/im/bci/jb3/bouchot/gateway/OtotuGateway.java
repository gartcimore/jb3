package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class OtotuGateway extends AbstractLegacyBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("ototu");
        conf.setGetUrl("https://ototu.euromussels.eu/goboard/backend/tsv");
        conf.setPostUrl("https://ototu.euromussels.eu/goboard/post");
        conf.setMessageContentParameterName("message");
        conf.setTagsEncoded(false);
        conf.setUsingXPost(false);
        conf.setLastIdParameterName("last");
        return conf;
    }

    public OtotuGateway() {
        super(createConf());
    }

}
