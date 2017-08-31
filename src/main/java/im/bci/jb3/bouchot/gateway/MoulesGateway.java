package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class MoulesGateway extends AbstractLegacyBouchotGateway {


    public MoulesGateway() {
        super("moules");
    }

}
