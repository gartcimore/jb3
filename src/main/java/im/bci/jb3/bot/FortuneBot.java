package im.bci.jb3.bot;

import im.bci.jb3.data.Fortune;
import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.Tribune;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FortuneBot implements Bot {

    @Autowired
    private Tribune tribune;

    @Value("${jb3.host}")
    private String host;

    private static final String NAME = "fortune";

    @Override
    public void handle(Post post) {
        try {
            if (BotUtils.isBotCall(post, NAME)) {
                Fortune fortune = tribune.fortune(Norloge.parseNorloges(post.getMessage()));
                if (null != fortune) {
                    tribune.post(NAME, new Norloge(post) + " La voilà " + host + "/fortune/" + fortune.getId());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FortuneBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
