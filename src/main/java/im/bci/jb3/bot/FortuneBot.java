package im.bci.jb3.bot;

import im.bci.jb3.bouchot.data.Fortune;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Norloge;
import im.bci.jb3.bouchot.logic.Tribune;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FortuneBot implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "fortune";

    @Override
    public void handle(Post post, UriComponentsBuilder uriBuilder) {
        try {
            if (tribune.isBotCall(post, NAME)) {
                Fortune fortune = tribune.fortune(post);
                if (null != fortune) {
                    tribune.post(NAME, Norloge.format(post) + " La voilà " + uriBuilder.path("/fortunes/" + fortune.getId()).build().toString(), post.getRoom());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FortuneBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
