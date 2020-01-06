package im.bci.jb3.bot;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Norloge;
import im.bci.jb3.bouchot.logic.Tribune;

@Component
public class CountDownBot implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "europe";
    private ArrayList<Timer> timers;
    private ScheduledExecutorService executor;


    @Override
    public void handle(Post post, UriComponentsBuilder uriBuilder) {
        try {
            if (tribune.isBotCall(post, NAME)) {
                final String command = post.getMessage().substring(0, 3);
                switch (command) {
                    case "clr":
                        for (Timer timer : timers) {
                            timer.cancel();
                        }
                        break;
                    case "add":
                        final String taskMessage = post.getMessage().substring(4);
                        TimerTask repeatedTask = new TimerTask() {
                            public void run() {
                                try {
                                    tribune.post(NAME, String.format("%s", taskMessage), post.getRoom());
                                } catch (Exception ex) {
                                    LogFactory.getLog(this.getClass()).error(String.format("%s bot error", NAME), ex);
                                }
                            }
                        };
                        Timer timer = new Timer("Timer" + command);
                        timers.add(timer);
                        long delay = 1000L;
                        long period = 1000L * 60L * 60L * 24L;
                        timer.scheduleAtFixedRate(repeatedTask, delay, period);

                    default:
                        LogFactory.getLog(this.getClass()).debug(String.format("%s unknown command '%s' for bot ", command, NAME));
                }
            }
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).error(String.format("%s bot error", NAME), ex);
        }
    }

    @PostConstruct
    void init() {
        executor = Executors.newSingleThreadScheduledExecutor();
        timers = new ArrayList<>();
    }

    @PreDestroy
    void clean() {
        executor.shutdown();
    }
}