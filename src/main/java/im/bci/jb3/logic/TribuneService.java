package im.bci.jb3.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.data.Post;
import im.bci.jb3.gateway.Gateways;
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
    private Gateways gateways;

    @Autowired
    private Tribune tribune;

    public Post post(String nickname, String message, String room) {
        if (!gateways.handlePost(nickname, message, room)) {
            Post post = tribune.post(nickname, message, room);
            if (null != post) {
                bots.handle(post);
            }
            return post;
        } else {
            return null;
        }
    }
}
