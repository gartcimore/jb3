package im.bci.jb3.b2b;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author devnewton
 */
@RestController
@RequestMapping("/b2b")
public class B2BController {

    @Autowired
    private PostRepository postPepository;

    @RequestMapping("/pull/{room}/{since}")
    public List<Post> pull(@PathVariable(name = "room", required = true) String room,
            @PathVariable(name = "since", required = true) DateTime since) {
        return postPepository.findPosts(since, DateTime.now().plusMinutes(5), room);
    }

}
