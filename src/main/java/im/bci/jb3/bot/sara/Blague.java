package im.bci.jb3.bot.sara;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Blague implements SaraAction {

    @Autowired
    private Tribune tribune;

    private static final int MAX_BLAGUE_LINES = 42;

    private static final Pattern BLAGUE = Pattern.compile("\\b(blague?s?|humours?|rires?)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public MatchLevel match(Post post) {
        return BLAGUE.matcher(post.getMessage()).find() ? MatchLevel.MUST : MatchLevel.NO;
    }

    @Override
    public boolean act(Post post, UriComponentsBuilder uriBuilder) {
        try {
            Document doc = Jsoup.connect("http://www.blague.org/").get();
            doc.select("#humour h1").remove();
            String blague = Jsoup.clean(doc.select("#humour").first().html(), "", Whitelist.none(),
                    new OutputSettings().prettyPrint(false));
            int linePosted = 0;
            for (String line : blague.split("\\n")) {
                if (linePosted++ >= MAX_BLAGUE_LINES) {
                    break;
                }
                tribune.post(Sara.NAME, line, post.getRoom());
            }
            return linePosted > 0;
        } catch (Exception ex) {
            Logger.getLogger(Blague.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
