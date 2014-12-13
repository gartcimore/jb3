package im.bci.jb3.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.data.Fortune;
import im.bci.jb3.data.FortuneRepository;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Tribune {

    @Autowired
    private PostRepository postPepository;

    @Autowired
    private FortuneRepository fortunePepository;

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt");
    private static final int MAX_POST_LENGTH = 512;
    private static final int MAX_NICKNAME_LENGTH = 32;

    public Post post(String nickname, String message) {
        if (null != nickname) {
            nickname = StringUtils.abbreviate(Jsoup.clean(nickname, Whitelist.none()), MAX_NICKNAME_LENGTH);
        }
        message = StringUtils.abbreviate(Jsoup.clean(message, messageWhitelist), MAX_POST_LENGTH);
        if (StringUtils.isNotBlank(message)) {
            Post post = new Post();
            post.setNickname(StringUtils.isNotBlank(nickname) ? nickname : "AnonymousCoward");
            post.setMessage(message);
            post.setTime(new Date());
            postPepository.save(post);
            return post;
        }
        return null;
    }

    public List<Post> get() {
        DateTime end = DateTime.now();
        DateTime start = end.minusWeeks(1);
        return postPepository.findPosts(start, end);
    }

    public Fortune fortune(List<Norloge> norloges) {
        Fortune f = new Fortune();
        f.setPosts(getForNorloges(norloges));
        return fortunePepository.save(f);
    }

    private List<Post> getForNorloges(List<Norloge> norloges) {
        List<Post> result = new ArrayList<Post>();
        for (Norloge norloge : norloges) {
            result.addAll(getForNorloge(norloge));
        }
        Collections.sort(result, new Comparator<Post>() {

            @Override
            public int compare(Post o1, Post o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        return result;
    }

    private List<Post> getForNorloge(Norloge norloge) {
        if (null == norloge.getBouchot()) {
            if (null != norloge.getId()) {
                final Post post = postPepository.findOne(norloge.getId());
                if (null != post) {
                    return Arrays.asList(post);
                }
            } else if (null != norloge.getTime()) {
                DateTime start = norloge.getTime();
                DateTime end = norloge.getTime().plusSeconds(1);
                return postPepository.findPosts(start, end);
            }
        }
        return Collections.emptyList();
    }

}
