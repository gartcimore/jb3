package im.bci.jb3.jobs;

import im.bci.jb3.data.PostRepository;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private PostRepository postPepository;

    @Scheduled(cron = "0 0/30 * * * *")
    public void deleteOldPosts() {
        Logger.getLogger(ScheduledTasks.class.getName()).info("Delete old posts.");
        postPepository.deleteOldPosts();
    }
}
