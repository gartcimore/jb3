package im.bci.jb3.bot;

import com.google.code.chatterbotapi.ChatterBotType;
import org.springframework.stereotype.Component;

@Component
public class Bob extends AbstractChatterBot {
    
    public Bob() throws Exception {
        super("bob", ChatterBotType.CLEVERBOT);
    }
}
