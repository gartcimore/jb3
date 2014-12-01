package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Tribune;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
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
    private Tribune tribune;


    @RequestMapping("/post")
    public void post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message) {
        tribune.post(nickname, message);
    }

    @RequestMapping(value = "/get")
    public Page<Post> get() {
        return tribune.get();
    }
}
