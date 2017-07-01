package im.bci.jb3.bot;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRevisor;
import im.bci.jb3.bouchot.logic.Norloge;
import im.bci.jb3.bouchot.logic.Tribune;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.LogFactory;

@Component
public class ReviseBot implements Bot {

    @Autowired
    private Tribune tribune;

    private static final Pattern REVISE_PATTERN = Pattern.compile("^\\/revise (#(?<id>\\w+\\s+))(?<revisedMessage>.*)$");

    @Override
    public void handle(final Post post, UriComponentsBuilder uriBuilder) {
        try {
            Matcher matcher = REVISE_PATTERN.matcher(post.getMessage());
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
            LogFactory.getLog(this.getClass()).error("revise bot error", ex);
        }
    }
}
