package im.bci.jb3.bouchot.gateway;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class GabuzomeuGateway extends AbstractWebdirectcoinGateway {
    
    private static Jb3BouchotConfig createConf() {
        Jb3BouchotConfig config = new Jb3BouchotConfig();
        config.setLocalRoom("gabuzomeu");
        config.setRemoteRoom("gabuzomeu");
        config.setWebdirectcoinURL("https://jb3.plop.cc/webdirectcoin");
        return config;
    }

    public GabuzomeuGateway() {
        super(createConf());
    }

}
