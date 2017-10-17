package im.bci.jb3.bouchot.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class Gateways {

    @Autowired
    private Gateway[] gateways;

    public boolean handlePost(String nickname, String message, String room, String auth) {
        boolean handled = false;
        for (Gateway gateway : gateways) {
            handled |= gateway.handlePost(nickname, message, room, auth);
        }
        return handled;
    }
}
