package im.bci.jb3.controllers;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.TribuneService;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 
 * @author devnewton
 */
@RestController
@RequestMapping("/restocoin")
public class RestocoinController {

    @Autowired
    private TribuneService tribune;

    @Autowired
    private PostRepository postRepository;

    private Period postsGetPeriod;
    
    @RequestMapping(path = "/post", method = RequestMethod.POST)
    public List<Post> post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message, @RequestParam(value = "room", required = false) String room, @RequestParam(value = "auth", required = false) String auth) {
        tribune.post(nickname, message, room, auth, ServletUriComponentsBuilder.fromCurrentRequest());
        return get(room);
    }

    @Value("${jb3.posts.get.period}")
    public void setPostsGetPeriod(String p) {
        postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
    }

    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public List<Post> get(@RequestParam(value = "room", required = false) String room) {
        DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
        DateTime start = end.minus(postsGetPeriod);
        return postRepository.findPosts(start, end, room);
    }
    
    @RequestMapping(path = "/get/{id}", method = RequestMethod.GET)
    public Post getOne(@PathVariable("id") String id) {
        return postRepository.findOne(id);
    }
}
