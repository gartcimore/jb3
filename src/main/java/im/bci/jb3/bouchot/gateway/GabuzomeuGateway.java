package im.bci.jb3.bouchot.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import im.bci.jb3.bouchot.data.GatewayPostId;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.data.PostRevision;
import im.bci.jb3.bouchot.legacy.LegacyUtils;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
//TODO @Component
public class GabuzomeuGateway extends WebSocketListener implements Gateway {

    private final Log LOGGER = LogFactory.getLog(this.getClass());

    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PostRepository postPepository;
    @Autowired
    protected LegacyUtils legacyUtils;

    private static final String DOMAIN = "https://jb3.plop.cc";
    private WebSocket ws;

    @PostConstruct
    public void connect() {
        Request request = new Request.Builder()
                .url(DOMAIN + "/webdirectcoin")
                .build();
        httpClient.newWebSocket(request, this);
    }

    @Override
    public synchronized void onOpen(WebSocket ws, Response response) {
        try {
            this.ws = ws;
            MessageC2S message = new MessageC2S();
            GetC2S get = new GetC2S();
            get.setRoom(this.getRoom());
            message.setGet(get);
            ws.send(objectMapper.writeValueAsString(message));
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
        return "gabuzomeu";
    }

    @Override
    public synchronized void post(String nickname, String messageBody, String auth) {
        try {
            if (null != this.ws) {
                MessageC2S message = new MessageC2S();
                PostC2S post = new PostC2S();
                post.setAuth(auth);
                post.setMessage(messageBody);//TODO convert norloges
                post.setNickname(nickname);
                post.setRoom(getRoom());
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
            GatewayPostId gatewayPostId = new GatewayPostId();
            gatewayPostId.setGateway(getRoom());
            gatewayPostId.setPostId(post.getId());
            if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                post.setId(null);
                post.setGatewayPostId(gatewayPostId);
                post.setRoom(getRoom());
                if (null != post.getRevisions()) {
                    for (PostRevision revision : post.getRevisions()) {
                        revision.setMessage(legacyUtils.convertFromLegacyNorloges(CleanUtils.cleanMessage(CleanUtils.truncateMessage(revision.getMessage())), revision.getTime(), getRoom()));
                    }
                }
                post.setNickname(CleanUtils.truncateNickname(post.getNickname()));
                post.setMessage(legacyUtils.convertFromLegacyNorloges(CleanUtils.cleanMessage(CleanUtils.truncateMessage(post.getMessage())), post.getTime(), getRoom()));
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
        this.connect();//TODO delay
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        this.connect();//TODO delay
    }

}
