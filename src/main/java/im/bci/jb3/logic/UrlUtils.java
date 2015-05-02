package im.bci.jb3.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class UrlUtils {

    private static final Pattern urlPattern = Pattern.compile("(https?|ftp|gopher)://[^\\s]+");

    public static void convertRawUrls(Document doc) {
        for (TextNode t : doc.body().textNodes()) {
            concertRawUrls(t);
        }
    }

    private static void concertRawUrls(TextNode textNode) {
        Matcher matcher = urlPattern.matcher(textNode.text());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, new Element(Tag.valueOf("a"), "").attr("href", matcher.group(0)).text("[url]").outerHtml());
        }
        matcher.appendTail(sb);
        textNode.after(sb.toString());
        textNode.remove();
    }
}
