package im.bci.jb3.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.data.Post;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class TribuneService {

    @Autowired
    private Bots bots;

    @Autowired
    private Tribune tribune;

    public void post(String nickname, String message) {
        Post post = tribune.post(nickname, message);
        if (null != post) {
            bots.handle(post);
        }
    }

    public List<Post> get() {
        return tribune.get();
    }
}