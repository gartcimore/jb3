package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import im.bci.jb3.bouchot.legacy.LegacyUtils;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
public class FormatDateTimeHelper implements Helper<DateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);

    @Override
    public CharSequence apply(DateTime dateTime, Options options) throws IOException {
        return new Handlebars.SafeString(formatter.print(dateTime));
    }

}
