package im.bci.jb3.bot.sara;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Norloge;
import im.bci.jb3.bouchot.logic.Tribune;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Moinsbete implements SaraAction {

    @Autowired
    private Tribune tribune;

    private static final Pattern JOURNEE = Pattern.compile("\\b(moins\\s*b[eê]tes?)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public MatchLevel match(Post post) {
        return JOURNEE.matcher(post.getMessage()).find() ? MatchLevel.MUST : MatchLevel.CAN;
    }

    @Override
    public boolean act(Post post, UriComponentsBuilder uriBuilder) {
        try {
            Document doc = Jsoup.connect("http://secouchermoinsbete.fr/random").get();
            Element anecdote = doc.select(".anecdote-content-wrapper .summary a").first();
            anecdote.select(".read-more").remove();
            if (StringUtils.isNotBlank(anecdote.text())) {
                String message = Norloge.format(post) + " " + anecdote.text();
                tribune.post(Sara.NAME, message, post.getRoom());
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(Moinsbete.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
