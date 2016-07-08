package im.bci.jb3.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import im.bci.jb3.bot.AbstractChatterBot;
import java.util.List;

import im.bci.jb3.data.Post;
import im.bci.jb3.websocket.messages.PresenceMsg;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebDirectCoinConnectedMoules {

    private static final TextMessage ACK_PRESENCE_MESSAGE = new TextMessage("[]");

    @Autowired
    private ObjectMapper objectMapper;

    private ConcurrentReferenceHashMap<WebSocketSession, String> moules = new ConcurrentReferenceHashMap<WebSocketSession, String>();

    public void send(List<Post> posts) {
        for (WebSocketSession moule : moules.keySet()) {
            try {
                if (null != moule) {
                    sendPostsToMoule(moule, posts);
                }
            } catch (Exception ex) {
                Logger.getLogger(AbstractChatterBot.class.getName()).log(Level.WARNING, null, ex);
            }
        }

    }

    void add(WebSocketSession session) {
        moules.put(session, session.getId());
    }

    void remove(WebSocketSession moule) throws JsonProcessingException {
        Presence presence = new Presence();
        notifyPresence(moule, presence);
        moules.remove(moule);
    }

    void sendPostsToMoule(WebSocketSession moule, List<Post> posts)
            throws JsonProcessingException, IOException {
        String payload = objectMapper.writeValueAsString(posts);
        moule.sendMessage(new TextMessage(payload));
    }

    public void ackMoulePresence(WebSocketSession moule, Presence presence) throws IOException {
        moule.getAttributes().put("moule-presence", presence);
        notifyPresence(moule, presence);
        moule.sendMessage(ACK_PRESENCE_MESSAGE);
    }

    private void notifyPresence(WebSocketSession moule, Presence presence) throws JsonProcessingException {
        PresenceMsg msg = new PresenceMsg();
        msg.setMouleId(moule.getId());
        msg.setPresence(presence);
        TextMessage message = new TextMessage(objectMapper.writeValueAsString(msg));
        for (WebSocketSession m : moules.keySet()) {
            try {
                if (null != m && !m.getId().equals(moule.getId())) {
                    m.sendMessage(message);
                }
            } catch (Exception ex) {
                Logger.getLogger(AbstractChatterBot.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}
