package im.bci.jb3.utils;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author devnewton
 */
public class Cleaner {

    public static String cleanInvalidChars(String message) {
        return StringEscapeUtils.escapeXml10(message);
    }
}
