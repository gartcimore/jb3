package im.bci.jb3.sse;

import java.util.Set;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseMoule {
    final Set<String> rooms;
    final SseEmitter emiter = new SseEmitter(15L * 60L * 1000L);

    public SseMoule(String[] rooms) {
        this.rooms = Set.of(rooms);
    }

}
