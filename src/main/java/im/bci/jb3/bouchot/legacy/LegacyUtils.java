package im.bci.jb3.bouchot.legacy;

import org.joda.time.DateTime;
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
    private FromLegacyPEGNorlogeConverter fromLegacyPEGNorlogeConverter;
    
    @Autowired
    private ToLegacyPEGNorlogeConverter toLegacyPEGNorlogeConverter;

    public static final String legacyTimezoneId = "Europe/Paris";
    public static final DateTimeZone legacyTimeZone = DateTimeZone.forID(legacyTimezoneId);
    public static final DateTimeFormatter legacyPostTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZone(legacyTimeZone);

    public String convertFromLegacyNorloges(String message, DateTime postTime, String room) {
        return fromLegacyPEGNorlogeConverter.convertFromLegacyNorloge(message, postTime, room);
    }

    public String convertToLegacyNorloges(String message, DateTime postTime, String room) {
        return toLegacyPEGNorlogeConverter.convertToLegacyNorloges(message, postTime, room);
    }

}
