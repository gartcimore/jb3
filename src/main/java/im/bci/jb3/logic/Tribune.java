package im.bci.jb3.logic;

import im.bci.jb3.data.Fortune;
import im.bci.jb3.data.FortuneRepository;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Tribune {

    @Autowired
    private PostRepository postPepository;

    @Autowired
    private FortuneRepository fortunePepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public Post post(String nickname, String message, String room) {
        nickname = CleanUtils.cleanNickname(nickname);
        room = CleanUtils.cleanRoom(room);
        message = CleanUtils.cleanMessage(message);
        if (StringUtils.isNotBlank(message)) {
            Post post = new Post();
            post.setNickname(StringUtils.isNotBlank(nickname) ? nickname : "AnonymousCoward");
            post.setMessage(message);
            post.setRoom(room);
            post.setTime(DateTime.now(DateTimeZone.UTC));
            postPepository.save(post);
            simpMessagingTemplate.convertAndSend("/topic/posts", Arrays.asList(post));
            return post;
        }
        return null;
    }

    public Fortune fortune(String message) {
        final List<Post> posts = new ArrayList<Post>();
        for (Element c : Jsoup.parseBodyFragment(message).select("c")) {
            Post referencedPost = this.postPepository.findOne(c.text());
            if (null != referencedPost) {
                posts.add(referencedPost);
            }
        }
        if (!posts.isEmpty()) {
            Fortune f = new Fortune();
            f.setPosts(posts);
            return fortunePepository.save(f);
        } else {
            return null;
        }
    }

    public boolean isBotCall(Post post, String botName) {
        if (botName.equals(post.getNickname())) {
            return false;
        }
        String message = post.getMessage();
        return message.contains(bigornoCall(botName)) || message.contains(ircCall(botName));
    }

    public boolean isReplyToBot(Post post, String botName) {
        for (Element c : Jsoup.parseBodyFragment(post.getMessage()).select("c")) {
            Post referencedPost = this.postPepository.findOne(c.text());
            if (botName.equals(referencedPost.getNickname())) {
                return true;
            }
        }
        return false;
    }

    public String messageWithoutBotCall(Post post, String botName) {
        String message = post.getMessage();
        message = StringUtils.removeStart(message, bigornoCall(botName));
        return message;
    }

    private static String bigornoCall(String botName) {
        return Jsoup.clean(botName + "<", Whitelist.none());
    }

    private static String ircCall(String botName) {
        return Jsoup.clean("/" + botName, Whitelist.none());
    }

}
