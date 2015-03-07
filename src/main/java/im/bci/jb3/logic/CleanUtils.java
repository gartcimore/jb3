package im.bci.jb3.logic;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class CleanUtils {

    private static final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt");
    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;
    private static final int MAX_ROOM_LENGTH = 32;

    public static String cleanMessage(String message) {
        message = StringUtils.abbreviate(Jsoup.clean(message, messageWhitelist), MAX_POST_LENGTH);
        return message;
    }

    public static String cleanRoom(String room) {
        if (null != room) {
            room = StringUtils.abbreviate(Jsoup.clean(room, Whitelist.none()), MAX_ROOM_LENGTH);
        }
        return room;
    }

    public static String cleanNickname(String nickname) {
        if (null != nickname) {
            nickname = StringUtils.abbreviate(Jsoup.clean(nickname, Whitelist.none()), MAX_NICKNAME_LENGTH);
        }
        return nickname;
    }

}
