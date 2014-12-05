package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.Tribune;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Fortune implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "fortune";

    @Override
    public void handle(Post post) {
        try {
            if (BotUtils.isBotCall(post, NAME)) {
                List<Post> posts =  tribune.getForNorloges(Norloge.parseNorloges(post.getMessage()));
                //TODO
            }
        } catch (Exception ex) {
            Logger.getLogger(Fortune.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
