package im.bci.jb3.bot.sara;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton
 */
@Component
public class AvenirEnCommun implements SaraAction {

    @Autowired
    private Tribune tribune;

    private static final Pattern PHI = Pattern.compile("\\b(jlm|jean\\sluc|((m[ée]l([ea]n|u))|holo)ch?(on|e)|avenir\\sen\\scommun|phi|france\\sinsoumise?|φ)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public MatchLevel match(Post post) {
        return PHI.matcher(post.getMessage()).find() ? MatchLevel.MUST : MatchLevel.NO;
    }

    @Override
    public boolean act(Post post, UriComponentsBuilder uriBuilder) {
        try {
            Document doc = Jsoup.connect("https://laec.fr/hasard").get();
            String sujet = doc.select("#contenu .subject-foreword").text();
            if (StringUtils.isNoneBlank(sujet)) {
                tribune.post(Sara.NAME, sujet, post.getRoom());
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(AvenirEnCommun.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
