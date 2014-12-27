package im.bci.jb3.utils;

import org.joda.time.DateTimeZone;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class TimezoneUtils {
    
    public static String javascriptTimezoneOffsetToJavaTimeZoneId(int offset) {
        return DateTimeZone.forOffsetMillis(-offset * 60 * 1000).toTimeZone().getID();
    }

}
