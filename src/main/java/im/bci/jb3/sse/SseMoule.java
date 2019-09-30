package im.bci.jb3.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseMoule {
	SseEmitter emiter = new SseEmitter(15L * 60L * 1000L);
	String nickname;

	public SseMoule(String nickname) {
		this.nickname = nickname;
	}

}
