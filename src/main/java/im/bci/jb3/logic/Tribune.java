package im.bci.jb3.logic;

import im.bci.jb3.bot.Bot;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class Tribune {

    @Autowired
    private PostRepository repository;

    @Autowired
    private Bot[] bots;

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt");
    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;

    public void post(String nickname, String message) {
        Post post = doPost(nickname, message);
        if (null != post) {
            for (Bot bot : bots) {
                bot.handle(post);
            }
        }
    }
    
    public void botPost(String nickname, String message) {
        doPost(nickname, message);
    }

    private Post doPost(String nickname, String message) {
        if (null != nickname) {
            nickname = StringUtils.abbreviate(Jsoup.clean(nickname, Whitelist.none()), MAX_NICKNAME_LENGTH);
        }
        message = StringUtils.abbreviate(Jsoup.clean(message, messageWhitelist), MAX_POST_LENGTH);
        message = Parser.unescapeEntities(message, false);
        if (StringUtils.isNotBlank(message)) {
            Post post = new Post();
            post.setNickname(StringUtils.isNotBlank(nickname) ? nickname : "AnonymousCoward");
            post.setMessage(message);
            post.setTime(new Date());
            repository.save(post);
            return post;
        }

        return null;
    }

    public Page<Post> get() {
        DateTime end = DateTime.now();
        DateTime start = end.minusWeeks(1);
        Page<Post> posts = repository.findPosts(start.toDate(), end.toDate(), new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        return posts;
    }

}
