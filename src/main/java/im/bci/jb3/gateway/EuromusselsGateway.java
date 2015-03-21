package im.bci.jb3.gateway;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class EuromusselsGateway extends AbstractBouchotGateway {

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void scheduledPostsImport() {
        importPosts();
    }
}
