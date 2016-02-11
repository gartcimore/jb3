package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Bots {

    @Autowired
    private Bot[] bots;

    @Async("botExecutor")
    public void handle(Post post, UriComponentsBuilder uriBuilder) {
        for (Bot bot : bots) {
            bot.handle(post, (UriComponentsBuilder)uriBuilder.clone());
        }
    }
}
