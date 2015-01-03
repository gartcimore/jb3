package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.frontend.RandomNicknameMV;
import im.bci.jb3.logic.TribuneService;
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
    public void post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message) {
        tribune.post(nickname, message);
    }

    @RequestMapping("/get")
    public List<Post> get() {
        DateTime end = DateTime.now(DateTimeZone.UTC);
        DateTime start = end.minusWeeks(1);
        return postRepository.findPosts(start, end);
    }

    private final DataFactory dataFactory = new DataFactory();

    @RequestMapping("/random-nickname")
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(dataFactory.getName());
        return mv;
    }
}
