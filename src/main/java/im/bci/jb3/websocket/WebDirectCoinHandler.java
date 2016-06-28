package im.bci.jb3.websocket;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import im.bci.jb3.bot.AbstractChatterBot;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.TribuneService;

@Component
public class WebDirectCoinHandler extends TextWebSocketHandler implements WebDirectCoinConnectedMoules {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TribuneService tribune;

	@Autowired
	private PostRepository postRepository;

	private Period postsGetPeriod;

	private ConcurrentReferenceHashMap<WebSocketSession, String> moules = new ConcurrentReferenceHashMap<WebSocketSession, String>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		moules.put(session, session.getId());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		moules.remove(session);
	}

	@Override
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

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		WebDirectCoinRQ webDirectCoinMessage = objectMapper.readValue(message.getPayload(), WebDirectCoinRQ.class);
		if (null != webDirectCoinMessage.getGet()) {
			get(session, webDirectCoinMessage.getGet());
		}
		if (null != webDirectCoinMessage.getPost()) {
			post(session, webDirectCoinMessage.getPost());
		}
		if (null != webDirectCoinMessage.getPresence()) {
			presence(session, webDirectCoinMessage.getPresence());
		}
	}

	private void presence(WebSocketSession session, PresenceRQ readValue) {
	}

	private void get(WebSocketSession moule, GetRQ rq) throws IOException {
		DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
		DateTime start = end.minus(postsGetPeriod);
		List<Post> posts = postRepository.findPosts(start, end, rq.getRoom());
		sendPostsToMoule(moule, posts);
		return;
	}

	private void sendPostsToMoule(WebSocketSession session, List<Post> posts)
			throws JsonProcessingException, IOException {
		String payload = objectMapper.writeValueAsString(posts);
		session.sendMessage(new TextMessage(payload));
	}

	private void post(WebSocketSession session, PostRQ rq) {
		UriComponentsBuilder uriBuilder = (UriComponentsBuilder) session.getAttributes()
				.get(WebDirectCoinSessionAttributes.URI_BUILDER);
		tribune.post(rq.getNickname(), rq.getMessage(), rq.getRoom(), rq.getAuth(), uriBuilder);
	}

	@Value("${jb3.posts.get.period}")
	public void setPostsGetPeriod(String p) {
		postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
	}

}
