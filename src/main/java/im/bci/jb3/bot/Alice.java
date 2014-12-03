package im.bci.jb3.bot;

import com.google.code.chatterbotapi.ChatterBotType;
import org.springframework.stereotype.Component;

@Component
public class Alice extends AbstractChatterBot {
    
    public Alice() throws Exception {
        super("alice", ChatterBotType.JABBERWACKY);
    }
}
