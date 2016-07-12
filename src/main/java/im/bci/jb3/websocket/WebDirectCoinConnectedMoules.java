package im.bci.jb3.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import im.bci.jb3.data.Post;
import im.bci.jb3.websocket.messages.MessageS2C;
import im.bci.jb3.websocket.messages.data.Presence;
import im.bci.jb3.websocket.messages.s2c.NorlogeS2C;
import im.bci.jb3.websocket.messages.s2c.PresenceS2C;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebDirectCoinConnectedMoules {

    @Autowired
    private ObjectMapper objectMapper;

    private TextMessage ackMessage;

    @PostConstruct
    public void setup() throws JsonProcessingException {
        MessageS2C message = new MessageS2C();
        message.setAck("k");
        ackMessage = new TextMessage(objectMapper.writeValueAsString(message));
    }

    private ConcurrentReferenceHashMap<WebSocketSession, String> moules = new ConcurrentReferenceHashMap<WebSocketSession, String>();

    public void send(List<Post> posts) {
        try {
            MessageS2C messageS2C = new MessageS2C();
            messageS2C.setPosts(posts);
            String payload = objectMapper.writeValueAsString(messageS2C);
            TextMessage message = new TextMessage(payload);
            for (WebSocketSession moule : moules.keySet()) {
                try {
                    if (null != moule) {
                        moule.sendMessage(message);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
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

    void sendPostsToMoule(WebSocketSession moule, List<Post> posts) throws JsonProcessingException, IOException {
        MessageS2C message = new MessageS2C();
        message.setPosts(posts);
        String payload = objectMapper.writeValueAsString(message);
        moule.sendMessage(new TextMessage(payload));
    }

    public void ackMoulePresence(WebSocketSession moule, Presence presence) throws IOException {
        moule.getAttributes().put("moule-presence", presence);
        notifyPresence(moule, presence);
        moule.sendMessage(ackMessage);
    }

    private void notifyPresence(WebSocketSession moule, Presence presence) throws JsonProcessingException {
        PresenceS2C presenceS2C = new PresenceS2C();
        presenceS2C.setMouleId(moule.getId());
        presenceS2C.setPresence(presence);
        MessageS2C messageS2C = new MessageS2C();
        messageS2C.setPresence(presenceS2C);
        TextMessage message = new TextMessage(objectMapper.writeValueAsString(messageS2C));
        for (WebSocketSession m : moules.keySet()) {
            try {
                if (null != m && !m.getId().equals(moule.getId())) {
                    m.sendMessage(message);
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public void sendNorloge(WebSocketSession moule, Post post) throws IOException {
        MessageS2C message = new MessageS2C();
        NorlogeS2C norloge = new NorlogeS2C();
        norloge.setMessageId(post.getId());
        norloge.setTime(post.getTime());
        message.setNorloge(norloge);
        String payload = objectMapper.writeValueAsString(message);
        moule.sendMessage(new TextMessage(payload));
    }
}
