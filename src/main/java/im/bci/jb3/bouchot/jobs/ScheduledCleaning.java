package im.bci.jb3.bouchot.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import im.bci.jb3.bouchot.data.PostRepository;

@Component
public class ScheduledCleaning {

    @Autowired
    private PostRepository postPepository;

    @Scheduled(cron = "0 3 * * * *")
    public void deleteOldPosts() {
        postPepository.deleteOldPosts();
    }
}
