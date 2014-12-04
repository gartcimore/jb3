package im.bci.jb3.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.jsoup.Jsoup;
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
    private Bots bots;

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt");
    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;

    public void post(String nickname, String message) {
        Post post = doPost(nickname, message);
        if (null != post) {
            bots.handle(post);
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

    public Page<Post> getForNorloge(String norloge) {
        DateTime end = DateTime.now();
        DateTime start = end.minusWeeks(1);
        Page<Post> posts = repository.findPosts(start.toDate(), end.toDate(), new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        return posts;
    }

}
