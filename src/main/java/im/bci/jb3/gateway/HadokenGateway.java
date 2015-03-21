package im.bci.jb3.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class HadokenGateway extends AbstractBouchotGateway {

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("hadoken");
        conf.setGetUrl("http://hadoken.free.fr/board/remote.php");
        conf.setPostUrl("http://hadoken.free.fr/board/post.php");
        conf.setLastIdParameterName("id");
        conf.setMessageContentParameterName("message");
        return conf;
    }

    public HadokenGateway() {
        super(createConf());
    }

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void scheduledPostsImport() {
        importPosts();
    }
}
