package im.bci.jb3.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

public class Norloge {

    private String id;
    private DateTime time;
    private String bouchot;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getBouchot() {
        return bouchot;
    }

    public void setBouchot(String bouchot) {
        this.bouchot = bouchot;
    }

    private static final DateTimeFormatter norlogeParser;
    private static final DateTimeFormatter norlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss");
    private static final Pattern idPattern = Pattern.compile("#(\\w)*(?!\\w*:)");

    static {
        final DateTimeParser norlogeYearParser = new DateTimeFormatterBuilder().appendYear(2, 4).appendLiteral('/').toParser();
        final DateTimeParser norlogeMonthDayParser = new DateTimeFormatterBuilder().appendMonthOfYear(2).appendLiteral('/').appendDayOfMonth(2).appendLiteral('#').toParser();
        final DateTimeParser norlogeHoursMinutesParser = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toParser();
        final DateTimeParser norlogeSecondsParser = new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).toParser();
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendOptional(norlogeYearParser).appendOptional(norlogeMonthDayParser).append(norlogeHoursMinutesParser).appendOptional(norlogeSecondsParser);
        norlogeParser = builder.toFormatter();
    }

    public static List<Norloge> parseNorloges(String message) {
        List<Norloge> result = new ArrayList<Norloge>();
        Matcher matcher = idPattern.matcher(message);
        while (matcher.find()) {
            Norloge norloge = new Norloge();
            norloge.setId(matcher.group(0).replace("#", ""));
            result.add(norloge);
        }
        int position = 0;
        while (position < message.length()) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            int newPosition;
            try {
                newPosition = norlogeParser.parseInto(norlogeTime, message, position);
            } catch (Exception e) {
                newPosition = -1;
            }
            if (newPosition >= 0) {
                Norloge norloge = new Norloge();
                norloge.setTime(norlogeTime.toDateTime());
                result.add(norloge);
                position = newPosition;
            } else {
                ++position;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(id)) {
            sb.append('#');
            sb.append(id);
        }
        if (null != time) {
            sb.append(norlogeFormatter.print(time));
        }
        if (StringUtils.isNotBlank(bouchot)) {
            sb.append('@').append(bouchot);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        final String post = "moules< 16:25:11 les norloges du style 12/02#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc #22:30 #tamaman #1234 #n3 ?";
        for (Norloge norloge : parseNorloges(post)) {
            System.out.println(norloge);
        }
    }

}
