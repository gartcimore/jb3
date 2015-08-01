package im.bci.jb3.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class MoulesGateway extends AbstractBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("moules");
        conf.setGetUrl("http://moules.org/board/backend/xml");
        conf.setPostUrl("http://moules.org/board/add.php");
        conf.setMessageContentParameterName("message");
        conf.setTagsEncoded(false);
        return conf;
    }

    public MoulesGateway() {
        super(createConf());
    }

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void scheduledPostsImport() {
        importPosts();
    }
}
