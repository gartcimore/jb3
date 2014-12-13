package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.TribuneService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author devnewton
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TribuneService tribune;

    @RequestMapping("/post")
    public void post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message) {
        tribune.post(nickname, message);
    }

    @RequestMapping(value = "/get")
    public List<Post> get() {
        return tribune.get();
    }
}
