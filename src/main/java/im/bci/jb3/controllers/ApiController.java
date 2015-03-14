package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.frontend.RandomNicknameMV;
import im.bci.jb3.logic.TribuneService;
import java.util.Collections;
import java.util.List;
import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

    @Autowired
    private PostRepository postRepository;

    
    @RequestMapping("/post")
    public List<Post> post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message, @RequestParam(value = "room", required = false) String room) {
        tribune.post(nickname, message, room);
        return get(room);
    }

    @RequestMapping("/get")
    public List<Post> get(@RequestParam(value = "room", required = false) String room) {
        DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
        DateTime start = end.minusWeeks(1);
        return postRepository.findPosts(start, end, room);
    }

    private final DataFactory dataFactory = new DataFactory();

    @RequestMapping("/random-nickname")
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(dataFactory.getName());
        return mv;
    }
}
