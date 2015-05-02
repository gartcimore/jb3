package im.bci.jb3.legacy;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.Norloge;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class LegacyUtils {

    @Autowired
    private PostRepository postPepository;

    public static final String legacyTimezoneId = "Europe/Paris";
    public static final DateTimeZone legacyTimeZone = DateTimeZone.forID(legacyTimezoneId);
    public static final DateTimeFormatter legacyPostTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZone(legacyTimeZone);
    
    public String convertFromLegacyPostable(final String room, final String message) {
      return convertFromLegacyPost(room, message);
    }

    public String convertFromLegacyPost(final String room, final String message) {
        final StringBuffer sb = new StringBuffer();
        LegacyParser.forEach(message, new LegacyProcessor() {

            @Override
            public void processNorloge(Norloge norloge, Matcher matcher) {
                if (StringUtils.isBlank(norloge.getBouchot())) {
                    DateTime time = norloge.getTime();
                    if (null != time) {
                        time = time.withZoneRetainFields(LegacyUtils.legacyTimeZone);
                        int maxDayBefore = norloge.getHasDay() ? 0 : 100;
                        for (int day = 0; day <= maxDayBefore; ++day) {
                            DateTime tryTime = time.minusDays(day);
                            Post post = postPepository.findOne(room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()));
                            if (null != post) {
                                matcher.appendReplacement(sb, Norloge.format(post));
                                return;
                            }
                        }
                    }
                    matcher.appendReplacement(sb, norloge.toString());
                } else {
                    matcher.appendReplacement(sb, "$0");
                }
            }

            @Override
            public void processTotoz(String totoz, Matcher matcher) {
                matcher.appendReplacement(sb, "<z>" + totoz + "</z>");
            }

            @Override
            public void processBigorno(String bigorno, Matcher matcher) {
                matcher.appendReplacement(sb, "<h>" + bigorno + "</h>");
            }

            @Override
            public void end(Matcher matcher) {
                matcher.appendTail(sb);
            }

        });
        return sb.toString();
    }

    private static final DateTimeFormatter toLegacyFullNorlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    private static final DateTimeFormatter toLegacyLongNorlogeFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    private static final DateTimeFormatter toLegacyNormalNorlogeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    private static final DateTimeFormatter toLegacyShortNorlogeFormatter = DateTimeFormat.forPattern("HH:mm").withZone(LegacyUtils.legacyTimeZone);

    public String convertToLegacyPost(String message, final DateTime postTime) {
        return convertToLegacyPostDoc(message, postTime).body().html();
    }

    public String convertToLegacyPostable(String message, final DateTime postTime) {
        Document doc = convertToLegacyPostDoc(message, postTime);
        for (Element a : doc.select("a")) {
            a.replaceWith(TextNode.createFromEncoded(a.attr("href"), null));
        }
        return doc.body().html();
    }

    private Document convertToLegacyPostDoc(String message, final DateTime postTime) {
        Document doc = Jsoup.parseBodyFragment(message);
        doc.outputSettings().prettyPrint(false);
        for (Element c : doc.select("c")) {
            Post post = postPepository.findOne(c.text());
            if (null != post) {
                final DateTime referencedPostTime = new DateTime(post.getTime());
                DateTimeFormatter formatter = findLegacyNorlogeFormatter(postTime, referencedPostTime);
                c.replaceWith(TextNode.createFromEncoded(formatter.print(referencedPostTime), null));
            }
        }
        for (Element t : doc.select("z")) {
            t.replaceWith(TextNode.createFromEncoded("[:" + t.text() + "]", null));
        }
        for (Element bi : doc.select("h")) {
            bi.replaceWith(TextNode.createFromEncoded(bi.text() + "&lt;", null));
        }
        return doc;
    }

    private static final DateTimeComparator dayComparator = DateTimeComparator.getDateOnlyInstance();
    private static final DateTimeComparator yearComparator = DateTimeComparator.getInstance(DateTimeFieldType.year());

    private DateTimeFormatter findLegacyNorlogeFormatter(DateTime postTime, DateTime referencedPostTime) {
        if (dayComparator.compare(referencedPostTime, postTime) == 0) {
            if (referencedPostTime.getSecondOfMinute() == 0) {
                return toLegacyShortNorlogeFormatter;
            } else {
                return toLegacyNormalNorlogeFormatter;
            }
        } else if (yearComparator.compare(referencedPostTime, postTime) == 0) {
            return toLegacyLongNorlogeFormatter;
        } else {
            return toLegacyFullNorlogeFormatter;
        }
    }

}
