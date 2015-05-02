package im.bci.jb3.legacy;

import im.bci.jb3.logic.Norloge;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class LegacyParser {

    private static final DateTimeFormatter norlogeParseFullFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter norlogeParseLongFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter norlogeParseNormalFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter norlogeParseShortFormatter = DateTimeFormat.forPattern("HH:mm").withZoneUTC();
    private static final Pattern norlogesPattern = Pattern.compile("(((?<bigorno>[a-zA-Z0-9-_]*)&lt;)|(\\[\\:(?<totoz>[^\\t\\)\\]]+)\\])|(?<time>(?<date>((?<year>\\d\\d\\d\\d)/)?(?:1[0-2]|0[1-9])/(?:3[0-1]|[1-2][0-9]|0[1-9])#)?((?:2[0-3]|[0-1][0-9])):([0-5][0-9])(:(?<seconds>[0-5][0-9]))?)(?<exp>[¹²³]|[:\\^][1-9]|[:\\^][1-9][0-9])?)(@(?<bouchot>[\\w.]+))?");

    public static void forEach(String message, LegacyProcessor processor) {
        Matcher matcher = norlogesPattern.matcher(message);
        while (matcher.find()) {
            String totoz = matcher.group("totoz");
            if (null != totoz) {
                processor.processTotoz(totoz, matcher);
            } else {
                String bigorno = matcher.group("bigorno");
                if (null != bigorno) {
                    processor.processBigorno(bigorno, matcher);
                } else {
                    String bouchot = matcher.group("bouchot");
                    Norloge norloge = null;
                    final String time = matcher.group("time");
                    if (null != time) {
                        norloge = parseNorlogeTime(time);
                        if (null != norloge) {
                            norloge.setBouchot(bouchot);
                        }
                    }
                    if (null != norloge) {
                        processor.processNorloge(norloge, matcher);
                    }
                }
            }
        }
        processor.end(matcher);
    }

    private static Norloge parseNorlogeTime(String item) {
        DateTime norlogeTime = parseNorlogeTimeWithFormat(item, norlogeParseFullFormatter);
        if (null != norlogeTime) {
            return new Norloge().withTime(norlogeTime).withHasYear(true).withHasMonth(true).withHasDay(true).withHasSeconds(true);
        }
        norlogeTime = parseNorlogeTimeWithFormat(item, norlogeParseLongFormatter);
        if (null != norlogeTime) {
            return new Norloge().withTime(norlogeTime).withHasMonth(true).withHasDay(true).withHasSeconds(true);
        }
        norlogeTime = parseNorlogeTimeWithFormat(item, norlogeParseNormalFormatter);
        if (null != norlogeTime) {
            return new Norloge().withTime(norlogeTime).withHasSeconds(true);
        }
        norlogeTime = parseNorlogeTimeWithFormat(item, norlogeParseShortFormatter);
        if (null != norlogeTime) {
            return new Norloge().withTime(norlogeTime);
        }
        return null;
    }

    private static DateTime parseNorlogeTimeWithFormat(String item, DateTimeFormatter format) {
        try {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            if (format.parseInto(norlogeTime, item, 0) >= 0) {
                return norlogeTime.toDateTime();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
