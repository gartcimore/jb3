package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRevisor;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.Tribune;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.unix4j.Unix4j;

import im.bci.jb3.logic.Norloge.ParsedNorloges;

@Component
public class SedBot implements Bot {

    @Autowired
    private Tribune tribune;

    @Override
    public void handle(final Post post, UriComponentsBuilder uriBuilder) {
        try {
            ParsedNorloges norloges = Norloge.parseNorloges(post.getMessage());
            String sedScript = norloges.getRemainingMessageContent();
            PostRevisor revisor = new PostRevisor().withPost(post);
            if (isValidSedScript(sedScript)) {
                List<Post> posts = tribune.getForNorloges(norloges);
                for (Post postToRevise : posts) {
                    tribune.revise(revisor, postToRevise, sed(postToRevise.getMessage(), sedScript));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SedBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String sed(String message, String sedScript) {
        return Unix4j.fromString(message).sed(sedScript).toStringResult();
    }

    private boolean isValidSedScript(String sedScript) {
        try {
            Unix4j.fromString("plop").sed(sedScript).toStringResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
