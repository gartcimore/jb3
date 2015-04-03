package im.bci.jb3.websocket;

import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Controller
public class WebDirectCoinController {

    @Autowired
    private PostRepository postRepository;
    private Period postsGetPeriod;

    @MessageMapping("/get")
    @SendTo("/topic/posts")
    public List<Post> get(GetRQ rq) {
        DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
        DateTime start = end.minus(postsGetPeriod);
        return postRepository.findPosts(start, end, rq.getRoom());
    }
    
    
    @Value("${jb3.posts.get.period}")
    public void setPostsGetPeriod(String p) {
        postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
    }

}
