package im.bci.jb3.logic;

import im.bci.jb3.Jb3Application;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimePrinter;

public class PostUtils {

    private static final DateTimeFormatter norlogeFormatter;

    static {
        final DateTimeParser norlogeYearParser = new DateTimeFormatterBuilder().appendYear(2, 4).appendLiteral('/').toParser();
        final DateTimeParser norlogeMonthDayParser = new DateTimeFormatterBuilder().appendMonthOfYear(2).appendLiteral('/').appendDayOfMonth(2).appendLiteral('#').toParser();
        final DateTimeParser norlogeHoursMinutesParser = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toParser();
        final DateTimeParser norlogeSecondsParser = new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).toParser();
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().appendOptional(norlogeYearParser).appendOptional(norlogeMonthDayParser).append(norlogeHoursMinutesParser).appendOptional(norlogeSecondsParser);
        norlogeFormatter = builder.toFormatter();
    }

    public static List<Date> listAllNorloge(String message) {
        List<Date> result= new ArrayList<Date>();
        int position = 0;
        while (position < message.length()) {
            MutableDateTime norloge = new MutableDateTime();
            int newPosition = norlogeFormatter.parseInto(norloge, message, position);
            if(newPosition >= 0) {
                result.add(norloge.toDate());
                position = newPosition;
            } else {
                ++position;
            }
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
       for( Date date : listAllNorloge("moules< 16:25:11 les norloges du style 12/02#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc ?")) {
           System.out.println(new DateTime(date));
       }
    }

}
