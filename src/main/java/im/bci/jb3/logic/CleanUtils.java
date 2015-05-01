package im.bci.jb3.logic;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class CleanUtils {

    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;
    private static final int MAX_ROOM_LENGTH = 32;

    public static String cleanMessage(String message) {
        message = StringUtils.abbreviate(message, MAX_POST_LENGTH);
        Document doc = Jsoup.parseBodyFragment(message);
        for (Element element : doc.body().children().select(":not(b,i,s,u,tt)")) {
            element.replaceWith(TextNode.createFromEncoded(element.toString(), null));
        }
        message = doc.body().html();
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
