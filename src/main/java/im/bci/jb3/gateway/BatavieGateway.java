package im.bci.jb3.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class BatavieGateway extends AbstractBouchotGateway {

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

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void scheduledPostsImport() {
        importPosts();
    }
}
