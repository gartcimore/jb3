package im.bci.jb3.gateway;

import org.apache.commons.lang3.StringUtils;
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
    
    public boolean handlePost(String nickname, String message, String room) {
        for(Gateway gateway : gateways) {
            if(StringUtils.equals(room, gateway.getRoom())) {
                gateway.post(nickname, message);
                return true;
            }
        }
        return false;
    }
}
