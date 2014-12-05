package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class BotUtils {

    public static boolean isBotCall(Post post, String botName) {
        if (botName.equals(post.getNickname())) {
            return false;
        }
        String message = post.getMessage();
        return message.contains(bigornoCall(botName)) || message.contains(hashTagCall(botName)) || message.contains(ircCall(botName));
    }

    static String messageWithoutBotCall(Post post, String botName) {
        String message = post.getMessage();
        message = StringUtils.removeStart(message, bigornoCall(botName));
        message = StringUtils.removeStart(message, hashTagCall(botName));
        return message;
    }

    private static String hashTagCall(String botName) {
        return Jsoup.clean("#" + botName, Whitelist.none());
    }

    private static String bigornoCall(String botName) {
        return Jsoup.clean(botName + "<", Whitelist.none());
    }

    private static String ircCall(String botName) {
        return Jsoup.clean("/" + botName, Whitelist.none());
    }

}
