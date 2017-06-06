package im.bci.jb3.bot.sara;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Define implements SaraAction {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "define";

    private static final Pattern DEFINE_PATTERN = Pattern.compile("^.*(define|what('s|\\sis))\\s*(?<word>.*)$");
    
    private final Random random = new Random();

    @Override
    public MatchLevel match(Post post) {
        return DEFINE_PATTERN.matcher(post.getMessage()).find() ? MatchLevel.MUST : MatchLevel.CAN;
    }

    @Override
    public boolean act(Post post, UriComponentsBuilder uriBuilder) {
        try {
            Matcher matcher = DEFINE_PATTERN.matcher(post.getMessage());
            if (matcher.matches()) {
                return postDefinition(matcher.group("word"), post);
            }
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).error("define error", ex);
        }
        return false;
    }

    private boolean postDefinition(String word, Post post) throws IOException {
        Connection connection = Jsoup.connect("https://www.urbandictionary.com/define.php");
        if(StringUtils.isNotBlank(word)) {
            connection.data("term", word);
        }
        Document doc = connection.get();
        Elements definitions = doc.select(".def-panel");
        if(definitions.isEmpty()) {
            return false;
        }
        Element definition = definitions.get(random.nextInt(definitions.size()));
        String text = definition.select(".word").text() + " " + definition.select(".meaning").text();
        String cleanedText = Jsoup.clean(text, Whitelist.none().addTags("a").addAttributes("a", "href"));
        tribune.post(Sara.NAME, cleanedText, post.getRoom());
        return true;
    }
}
