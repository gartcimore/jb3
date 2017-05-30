package im.bci.jb3.b2b;

import im.bci.jb3.bouchot.gateway.Gateway;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
//TODO @Component
public class B2BGateway implements Gateway {

    @Autowired
    private B2BConfig peers;

    @PostConstruct
    public void plop() {
        System.out.println(peers);
    }

    @Override
    public void post(String nickname, String message, String auth) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
