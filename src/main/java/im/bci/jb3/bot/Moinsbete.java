package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Tribune;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Moinsbete implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "moinsbete";
    private static final String CALL = Jsoup.clean(NAME + "<", Whitelist.none());

    @Override
    public void handle(Post post) {
        try {
            if (!NAME.equals(post.getNickname()) && post.getMessage().contains(CALL)) {

                Document doc = Jsoup.connect("http://secouchermoinsbete.fr/random").get();
                Element anecdote = doc.select(".anecdote-content-wrapper .summary a").first();
                anecdote.select(".read-more").remove();
                String message = anecdote.text();
                tribune.botPost(NAME, message);
            }
        } catch (IOException ex) {
            Logger.getLogger(Moinsbete.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
