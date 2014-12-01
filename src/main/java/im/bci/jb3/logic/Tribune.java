package im.bci.jb3.logic;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt");
    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;

    public void post(String nickname, String message) {
        if (null != nickname) {
            nickname = StringUtils.abbreviate(Jsoup.clean(nickname, Whitelist.none()), MAX_NICKNAME_LENGTH);
        }
        message = StringUtils.abbreviate(Jsoup.clean(message, messageWhitelist), MAX_POST_LENGTH);
        if (StringUtils.isNotBlank(message)) {
            Post plop = new Post();
            plop.setNickname(StringUtils.isNotBlank(nickname) ? nickname : "AnonymousCoward");
            plop.setMessage(message);
            plop.setTime(new Date());
            repository.save(plop);
        }
    }

    public Page<Post> get() {
        DateTime end = DateTime.now();
        DateTime start = end.minusWeeks(1);
        Page<Post> posts = repository.findPosts(start.toDate(), end.toDate(), new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        return posts;
    }

}
