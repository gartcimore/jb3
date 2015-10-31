package im.bci.jb3.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class SveetchGateway extends AbstractBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("sveetch");
        conf.setGetUrl("http://sveetch.net/tribune/remote/xml/");
        conf.setPostUrl("http://sveetch.net/tribune/post/xml/");
        conf.setLastIdParameterName("last_id");
        conf.setMessageContentParameterName("content");
        conf.setTagsEncoded(false);
        return conf;
    }

    public SveetchGateway() {
        super(createConf());
    }

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void scheduledPostsImport() {
        importPosts();
    }
}
