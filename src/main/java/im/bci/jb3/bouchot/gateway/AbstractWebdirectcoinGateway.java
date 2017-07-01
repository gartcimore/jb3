package im.bci.jb3.bouchot.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.data.PostRevision;
import im.bci.jb3.bouchot.logic.CleanUtils;
import im.bci.jb3.bouchot.websocket.messages.MessageC2S;
import im.bci.jb3.bouchot.websocket.messages.MessageS2C;
import im.bci.jb3.bouchot.websocket.messages.c2s.GetC2S;
import im.bci.jb3.bouchot.websocket.messages.c2s.PostC2S;
import im.bci.jb3.event.NewPostsEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

/**
 *
 * @author devnewton
 */
public abstract class AbstractWebdirectcoinGateway extends WebSocketListener implements Gateway {

    private final Log LOGGER = LogFactory.getLog(this.getClass());
    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PostRepository postPepository;
    @Resource(name = "mouleScheduler")
    private TaskScheduler scheduler;
    private WebSocket ws;
    private final Jb3BouchotConfig config;
    private int nbConnexionFailOrClose;

    public AbstractWebdirectcoinGateway(Jb3BouchotConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void connect() {
        scheduler.schedule(() -> {
            Request request = new Request.Builder().url(config.getWebdirectcoinURL()).build();
            httpClient.newWebSocket(request, this);
        }, DateTime.now().plusMinutes(nbConnexionFailOrClose).toDate());
    }

    @Override
    public synchronized void onOpen(WebSocket ws, Response response) {
        try {
            nbConnexionFailOrClose = Math.max(0, nbConnexionFailOrClose - 1);
            this.ws = ws;
            MessageC2S message = new MessageC2S();
            GetC2S get = new GetC2S();
            get.setRoom(this.getRoom());
            message.setGet(get);
            ws.send(objectMapper.writeValueAsString(message));
            LOGGER.info("Connected to " + getRoom());
        } catch (JsonProcessingException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            MessageS2C webDirectCoinMessage = objectMapper.readValue(text, MessageS2C.class);
            if (null != webDirectCoinMessage.getPosts()) {
                importPosts(webDirectCoinMessage.getPosts());
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public String getRoom() {
        return config.getLocalRoom();
    }

    @Override
    public synchronized void post(String nickname, String messageBody, String auth) {
        try {
            if (null != this.ws) {
                MessageC2S message = new MessageC2S();
                PostC2S post = new PostC2S();
                post.setAuth(auth);
                post.setMessage(messageBody);
                post.setNickname(nickname);
                post.setRoom(config.getRemoteRoom());
                message.setPost(post);
                ws.send(objectMapper.writeValueAsString(message));
            }
        } catch (JsonProcessingException ex) {
            LOGGER.error(ex);
        }
    }

    private synchronized void importPosts(List<Post> posts) {
        ArrayList<Post> newPosts = new ArrayList<>();
        for (Post post : posts) {
            post.setId(CleanUtils.truncateId(post.getId()));
            if (StringUtils.equals(config.getRemoteRoom(), post.getRoom())) {
                    post.setRoom(config.getLocalRoom());
                    post.setNickname(CleanUtils.truncateNickname(post.getNickname()));
                    post.setMessage(CleanUtils.truncateMessage(post.getMessage()));
                    if (null != post.getRevisions()) {
                        for (PostRevision revision : post.getRevisions()) {
                            revision.setMessage(CleanUtils.truncateMessage(revision.getMessage()));
                        }
                    }
                    postPepository.save(post);
                    newPosts.add(post);
            }
        }
        if (!newPosts.isEmpty()) {
            publisher.publishEvent(new NewPostsEvent(newPosts));
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        LOGGER.info("Disconnected from " + getRoom() + ": " + code + " " + reason);
        nbConnexionFailOrClose = Math.min(30, nbConnexionFailOrClose + 1);
        this.connect();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        LOGGER.error("Connection failure from " + getRoom(), t);
        nbConnexionFailOrClose = Math.min(30, nbConnexionFailOrClose + 1);
        this.connect();
    }

}
