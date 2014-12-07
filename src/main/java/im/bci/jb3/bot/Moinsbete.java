package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.Tribune;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Moinsbete implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "moinsbete";

    @Override
    public void handle(Post post) {
        try {
            if (BotUtils.isBotCall(post, NAME)) {
                Document doc = Jsoup.connect("http://secouchermoinsbete.fr/random").get();
                Element anecdote = doc.select(".anecdote-content-wrapper .summary a").first();
                anecdote.select(".read-more").remove();
                String message = new Norloge(post) + " " + anecdote.text();
                tribune.botPost(NAME, message);
            }
        } catch (Exception ex) {
            Logger.getLogger(Moinsbete.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
