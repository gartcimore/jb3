package im.bci.jb3.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.data.Post;
import im.bci.jb3.gateway.Gateways;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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

    public Post post(String nickname, String message, String room, String auth, UriComponentsBuilder uriBuilder) {
        if (!gateways.handlePost(nickname, message, room, auth)) {
            Post post = tribune.post(nickname, message, room);
            if (null != post) {
                bots.handle(post, uriBuilder);
            }
            return post;
        } else {
            return null;
        }
    }
}
