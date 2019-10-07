package im.bci.jb3.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.CleanUtils;
import im.bci.jb3.bouchot.websocket.messages.data.Presence;

@Component
@Controller
public class SseCoinController {

    @Autowired
    private SseCoinService service;

    @GetMapping("/ssecoin/posts")
    public SseEmitter posts(@RequestParam(value = "rooms", required = true) String[] rooms) {
        SseMoule moule = service.addMoule(rooms);
        service.emitPosts(moule);
        return moule.emiter;
    }
    
    @PostMapping(value = "/ssecoin/presence")
    @ResponseBody
    public void presence(String nickname, String status) {
        Presence presence = new Presence();
        presence.setNickname(CleanUtils.truncateAndCleanNickname(nickname));
        presence.setStatus(CleanUtils.truncateAndCleanStatus(status));
        service.broadcastPresence(presence);
    }

    @PostMapping(value = "/ssecoin/posts")
    @ResponseBody
    public void post(@RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "room", required = false) String room,
            @RequestParam(value = "auth", required = false) String auth) {
        service.post(nickname, message, room, auth);
    }

    @GetMapping(path = "/ssecoin/posts/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Post findById(@PathVariable String messageId) {
        return service.findOne(messageId);
    }

}
