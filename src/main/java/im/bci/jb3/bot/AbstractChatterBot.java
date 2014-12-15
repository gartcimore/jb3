package im.bci.jb3.bot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.Tribune;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractChatterBot implements Bot {

    private static final ChatterBotFactory factory = new ChatterBotFactory();

    private final String name;
    private final ChatterBotType type;
    private ChatterBotSession session;

    @Autowired
    private Tribune tribune;

    protected AbstractChatterBot(String name, ChatterBotType type) throws Exception {
        this.name = name;
        this.type = type;
    }

    @Override
    public void handle(Post post) {
        try {
            if (tribune.isBotCall(post, name)) {
                if (null == session) {
                    ChatterBot bot = factory.create(type);
                    session = bot.createSession();
                }
                final String message = tribune.messageWithoutBotCall(post, name);
                tribune.post(name, new Norloge(post) + " " + session.think(message));
            }
        } catch (Exception ex) {
            session = null;
            Logger.getLogger(AbstractChatterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
