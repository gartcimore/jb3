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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ReviseBot implements Bot {

    @Autowired
    private Tribune tribune;

    private static final Pattern revisePattern = Pattern.compile("^\\/revise (#(?<id>\\w+))(?<revisedMessage>.*)$");

    @Override
    public void handle(final Post post, UriComponentsBuilder uriBuilder) {
        try {
            Matcher matcher = revisePattern.matcher(post.getMessage());
            if (matcher.matches()) {
                String id = matcher.group("id");
                String revisedMessage = matcher.group("revisedMessage");
                List<Post> posts = tribune.getForNorloge(new Norloge().withId(id));
                PostRevisor revisor = new PostRevisor().withPost(post);
                for (Post postToRevise : posts) {
                    tribune.revise(revisor, postToRevise, revisedMessage);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ReviseBot.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
