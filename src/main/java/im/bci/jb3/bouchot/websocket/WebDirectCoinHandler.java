package im.bci.jb3.bouchot.websocket;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.logic.UserPostHandler;
import im.bci.jb3.bouchot.websocket.messages.MessageC2S;
import im.bci.jb3.bouchot.websocket.messages.c2s.GetC2S;
import im.bci.jb3.bouchot.websocket.messages.c2s.GetNorlogeC2S;
import im.bci.jb3.bouchot.websocket.messages.c2s.PostC2S;
import im.bci.jb3.bouchot.websocket.messages.data.Presence;

@Component
public class WebDirectCoinHandler extends TextWebSocketHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserPostHandler tribune;

	@Autowired
	private PostRepository postRepository;

	private Period postsGetPeriod;

	@Autowired
	private WebDirectCoinConnectedMoules moules;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		moules.add(session);
		System.out.println("moule " + moules.printableMouleId(session) + " connected");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		moules.remove(session);
		System.out.println("moule " + moules.printableMouleId(session) + " disconnected");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		MessageC2S webDirectCoinMessage = objectMapper.readValue(message.getPayload(), MessageC2S.class);
		if (null != webDirectCoinMessage.getGet()) {
			get(session, webDirectCoinMessage.getGet());
		}
		if (null != webDirectCoinMessage.getPost()) {
			post(session, webDirectCoinMessage.getPost());
		}
		if (null != webDirectCoinMessage.getPresence()) {
			presence(session, webDirectCoinMessage.getPresence());
		}
		if (null != webDirectCoinMessage.getGetNorloge()) {
			getNorloge(session, webDirectCoinMessage.getGetNorloge());
		}
	}

	private void getNorloge(WebSocketSession moule, GetNorlogeC2S getNorloge) throws IOException {
		Post post = postRepository.findOne(getNorloge.getMessageId());
		if (null != post) {
			moules.sendNorloge(moule, post);
		}
	}

	private void presence(WebSocketSession moule, Presence presence) throws IOException {
		moules.ackMoulePresence(moule, presence);
	}

	private void get(WebSocketSession moule, GetC2S rq) throws IOException {
		DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
		DateTime start = end.minus(postsGetPeriod);
		List<Post> posts = postRepository.findPosts(start, end, rq.getRoom());
		moules.sendPostsToMoule(moule, posts);
		return;
	}

	private void post(WebSocketSession session, PostC2S rq) {
		UriComponentsBuilder uriBuilder = (UriComponentsBuilder) session.getAttributes()
				.get(WebDirectCoinSessionAttributes.URI_BUILDER);
		tribune.post(rq.getNickname(), rq.getMessage(), rq.getRoom(), rq.getAuth(), uriBuilder);
	}

	@Value("${jb3.posts.get.period}")
	public void setPostsGetPeriod(String p) {
		postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
	}

}