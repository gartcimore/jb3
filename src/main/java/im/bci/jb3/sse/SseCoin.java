package im.bci.jb3.sse;

import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.UserPostHandler;
import im.bci.jb3.event.NewPostsEvent;

@Component
@Controller
public class SseCoin {

	@Autowired
	private UserPostHandler tribune;

	private CopyOnWriteArraySet<SseMoule> moules = new CopyOnWriteArraySet<>();

	@GetMapping("/ssecoin")
	public SseEmitter sse(@RequestParam(value = "nickname", required = false) String nickname) {
		SseMoule moule = new SseMoule(nickname);
		moules.add(moule);
		moule.emiter.onCompletion(() -> moules.remove(moule));
		return moule.emiter;
	}

	@PostMapping(value = "/ssecoin")
	public void post(@RequestParam(value = "nickname", required = false) String nickname,
			@RequestParam(value = "message") String message,
			@RequestParam(value = "room", required = false) String room,
			@RequestParam(value = "auth", required = false) String auth) {
		tribune.post(nickname, message, room, auth, ServletUriComponentsBuilder.fromCurrentRequest());
	}

	@EventListener
	public void notify(NewPostsEvent event) {
		for (SseMoule moule : moules) {
			try {
				for (Post post : event.getPosts()) {
					moule.emiter.send(post);
				}
			} catch (Exception e) {
				moules.remove(moule);
			}
		}
	}
}
