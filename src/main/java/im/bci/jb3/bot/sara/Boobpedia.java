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
public class Boobpedia implements SaraAction {

    @Autowired
    private Tribune tribune;

    private static final Pattern BOOB = Pattern.compile("\\b(boob?s?|seins?|nichons?|poitrines?)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public MatchLevel match(Post post) {
        return BOOB.matcher(post.getMessage()).find() ? MatchLevel.MUST : MatchLevel.NO;
    }

    @Override
    public boolean act(Post post, UriComponentsBuilder uriBuilder) {
        try {
            Document doc = Jsoup.connect("http://www.boobpedia.com/boobs/Special:Random").timeout(30000).get();
            String boobText = doc.select("#mw-content-text > p:nth-child(2)").text();
            String boobImageSrc = doc.select(".image > img:nth-child(1)").attr("src");
            String boobURI = UriComponentsBuilder.fromUriString("http://www.boobpedia.com").path(boobImageSrc).build().encode().toString();
            if (StringUtils.isNoneBlank(boobText) && StringUtils.isNoneBlank(boobURI)) {
                tribune.post(Sara.NAME, boobText + " " + boobURI, post.getRoom());
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(Boobpedia.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
