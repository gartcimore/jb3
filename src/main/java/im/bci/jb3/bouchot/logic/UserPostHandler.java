package im.bci.jb3.bouchot.logic;

import im.bci.jb3.bot.Bots;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.gateway.Gateways;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class UserPostHandler {

    @Autowired
    private Bots bots;

    @Autowired
    private Gateways gateways;

    @Autowired
    private Tribune tribune;

    public Post post(String nickname, String message, String room, String auth, UriComponentsBuilder uriBuilder) {
        if (!gateways.handlePost(nickname, message, room, auth)) {
            Post post = tribune.post(nickname, message, room);
            if (null != post && null != uriBuilder) {//TODO better way to retrieve uriBuilder
                bots.handle(post, uriBuilder);
            }
            return post;
        } else {
            return null;
        }
    }
}
