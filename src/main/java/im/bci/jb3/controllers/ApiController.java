package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.frontend.RandomNicknameMV;
import im.bci.jb3.logic.TribuneService;

import java.util.List;

import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final DataFactory dataFactory = new DataFactory();

    private Period postsGetPeriod;

    public ApiController() {
        dataFactory.randomize(DateTime.now().getMillisOfDay());
    }

    @RequestMapping("/post")
    public List<Post> post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message, @RequestParam(value = "room", required = false) String room, @RequestParam(value = "auth", required = false) String auth) {
        tribune.post(nickname, message, room, auth);
        return get(room);
    }

    @Value("${jb3.posts.get.period}")
    public void setPostsGetPeriod(String p) {
        postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
    }

    @RequestMapping("/get")
    public List<Post> get(@RequestParam(value = "room", required = false) String room) {
        DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
        DateTime start = end.minus(postsGetPeriod);
        return postRepository.findPosts(start, end, room);
    }

    @RequestMapping("/random-nickname")
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(dataFactory.getName());
        return mv;
    }
}
