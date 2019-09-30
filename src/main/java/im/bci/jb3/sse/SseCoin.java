package im.bci.jb3.sse;

import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.event.NewPostsEvent;

@Component
@Controller
public class SseCoin {

	private CopyOnWriteArraySet<SseMoule> moules = new CopyOnWriteArraySet<>();

	@GetMapping("/sse")
	public SseEmitter sse(@RequestParam(value = "nickname", required = false) String nickname) {
		SseMoule moule = new SseMoule(nickname);
		moules.add(moule);
		moule.emiter.onCompletion(() -> moules.remove(moule));
		return moule.emiter;
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
