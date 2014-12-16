package im.bci.jb3.logic;

import im.bci.jb3.data.Post;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Norloge {

    private String id;
    private DateTime time;
    private String bouchot;

    public Norloge(Post post) {
        this.id = post.getId();
    }

    public Norloge() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Norloge withId(String i) {
        setId(i);
        return this;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public Norloge withTime(DateTime t) {
        setTime(t);
        return this;
    }

    public Norloge withTime(Date t) {
        setTime(new DateTime(t));
        return this;
    }

    public String getBouchot() {
        return bouchot;
    }

    public void setBouchot(String bouchot) {
        this.bouchot = bouchot;
    }

    public Norloge withBouchot(String b) {
        setBouchot(b);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 67 * hash + (this.bouchot != null ? this.bouchot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Norloge other = (Norloge) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return !((this.bouchot == null) ? (other.bouchot != null) : !this.bouchot.equals(other.bouchot));
    }

    private static final DateTimeFormatter norlogePrintFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC();
    private static final List<DateTimeFormatter> norlogeParseFormatters = Arrays.asList(DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC(), DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZoneUTC(), DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC(), DateTimeFormat.forPattern("HH:mm").withZoneUTC());
    private static final Pattern norlogesPattern = Pattern.compile("((#(?<id>\\w+))|(?<time>(?<date>((?<year>\\d\\d\\d\\d)/)?(?:1[0-2]|0[1-9])/(?:3[0-1]|[1-2][0-9]|0[1-9])#)?((?:2[0-3]|[0-1][0-9])):([0-5][0-9])(:(?<seconds>[0-5][0-9]))?)(?<exp>[¹²³]|[:\\^][1-9]|[:\\^][1-9][0-9])?)(@(?<bouchot>[\\w.]+))?");

    public static List<Norloge> parseNorloges(String message) {
        final List<Norloge> result = new ArrayList<Norloge>();
        forEachNorloge(message, new NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                result.add(norloge);
            }

            @Override
            public void end(Matcher matcher) {
            }
        });
        return result;
    }

    public interface NorlogeProcessor {

        void process(Norloge norloge, Matcher matcher);

        void end(Matcher matcher);
    }

    public static void forEachNorloge(String message, NorlogeProcessor processor) {
        Matcher matcher = norlogesPattern.matcher(message);
        while (matcher.find()) {
            String id = matcher.group("id");
            String bouchot = matcher.group("bouchot");
            Norloge norloge = null;
            if (null != id) {
                norloge = new Norloge().withId(id).withBouchot(bouchot);
            } else {
                final String time = matcher.group("time");
                if (null != time) {
                    DateTime norlogeTime = parseNorlogeTime(time);
                    if (null != norlogeTime) {
                        norloge = new Norloge().withTime(norlogeTime).withBouchot(bouchot);
                    }
                }
            }
            if (null != norloge) {
                processor.process(norloge, matcher);
            }
        }
        processor.end(matcher);
    }

    private static DateTime parseNorlogeTime(String item) {
        for (DateTimeFormatter format : norlogeParseFormatters) {
            DateTime norlogeTime = parseNorlogeTimeWithFormat(item, format);
            if (null != norlogeTime) {
                return norlogeTime;
            }
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

    public static String format(Post post) {
        return '#' + post.getId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(id)) {
            sb.append('#');
            sb.append(id);
        }
        if (null != time) {
            sb.append(norlogePrintFormatter.print(time));
        }
        if (StringUtils.isNotBlank(bouchot)) {
            sb.append('@').append(bouchot);
        }
        return sb.toString();
    }
}
