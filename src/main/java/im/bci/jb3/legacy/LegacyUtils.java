package im.bci.jb3.legacy;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.Norloge;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    public String convertFromLegacyNorloges(String message) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                if (StringUtils.isBlank(norloge.getBouchot())) {
                    DateTime time = norloge.getTime();
                    if (null != time) {
                        time = time.withZoneRetainFields(LegacyUtils.legacyTimeZone);
                        Post post = postPepository.findOne(time, time.plusSeconds(1));
                        if (null != post) {
                            matcher.appendReplacement(sb, Norloge.format(post));
                            return;
                        }
                    }
                    matcher.appendReplacement(sb, norloge.toString());
                } else {
                    matcher.appendReplacement(sb, "$0");
                }
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

    public String convertToLegacyNorloges(String message, final DateTime postTime) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                if (null != norloge.getId()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        final DateTime referencedPostTime = new DateTime(post.getTime());
                        DateTimeFormatter formatter = findLegacyNorlogeFormatter(postTime, referencedPostTime);
                        matcher.appendReplacement(sb, formatter.print(referencedPostTime));
                        return;
                    }
                }
                matcher.appendReplacement(sb, "$0");
            }

            @Override
            public void end(Matcher matcher) {
                matcher.appendTail(sb);
            }

        });
        return sb.toString();
    }

    private DateTimeFormatter findLegacyNorlogeFormatter(DateTime postTime, DateTime referencedPostTime) {
        if (Days.daysBetween(postTime, referencedPostTime).isLessThan(Days.ONE)) {
            if (referencedPostTime.getSecondOfMinute() == 0) {
                return toLegacyShortNorlogeFormatter;
            } else {
                return toLegacyNormalNorlogeFormatter;
            }
        } else if (Years.yearsBetween(postTime, referencedPostTime).isLessThan(Years.ONE)) {
            return toLegacyLongNorlogeFormatter;
        } else {
            return toLegacyFullNorlogeFormatter;
        }
    }

}
