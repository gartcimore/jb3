package im.bci.jb3.bouchot.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@ConditionalOnExpression("'${jb3.defaults.room:}' != 'devnewton'")
@Component
public class DevnewtonGateway extends AbstractWebdirectcoinGateway {
    
    private static Jb3BouchotConfig createConf() {
        Jb3BouchotConfig config = new Jb3BouchotConfig();
        config.setLocalRoom("devnewton");
        config.setRemoteRoom("devnewton");
        config.setWebdirectcoinURL("https://b3.bci.im/webdirectcoin");
        return config;
    }

    public DevnewtonGateway() {
        super(createConf());
    }

}
