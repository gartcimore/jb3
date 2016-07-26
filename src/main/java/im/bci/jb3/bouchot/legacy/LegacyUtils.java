package im.bci.jb3.bouchot.legacy;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.logic.Norloge;

import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
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

    public String convertFromLegacyNorloges(final String room, final String message) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                DateTime time = norloge.getTime();
                if (null != time) {
                    time = time.withZoneRetainFields(LegacyUtils.legacyTimeZone);
                    int maxDayBefore = norloge.getHasDay() ? 0 : 100;
                    for (int day = 0; day <= maxDayBefore; ++day) {
                        DateTime tryTime = time.minusDays(day);
                        Post post = postPepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()));
                        if (null != post) {
                            matcher.appendReplacement(sb, Norloge.format(post));
                            return;
                        }
                    }
                }
                matcher.appendReplacement(sb, norloge.toString());
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

    public String convertToLegacyNorloges(String message, final DateTime postTime, final String room) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                if (null != norloge.getId()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        final DateTime referencedPostTime = new DateTime(post.getTime());
                        DateTimeFormatter formatter = findLegacyNorlogeFormatter(postTime, referencedPostTime);
                        String legacyNorloge = formatter.print(referencedPostTime);
                        if(!StringUtils.equals(post.getRoom(), room)) {
                            legacyNorloge += "@" + post.getRoom();
                        }
                        matcher.appendReplacement(sb, legacyNorloge);
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
