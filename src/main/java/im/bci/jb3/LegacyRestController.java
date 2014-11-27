package im.bci.jb3;

import im.bci.jb3.backend.legacy.LegacyBoard;
import im.bci.jb3.backend.legacy.LegacyPost;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class LegacyRestController {

    @Autowired
    private PostRepository repository;

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s");

    @RequestMapping("/legacy/post")
    public void post(@RequestParam(value = "nickname") String nickname, @RequestParam(value = "message") String message) {
        nickname = Jsoup.clean(nickname, Whitelist.none());
        message = Jsoup.clean(message, messageWhitelist);
        if (StringUtils.hasText(message)) {
            Post plop = new Post();
            plop.setNickname(StringUtils.hasText(nickname) ? nickname : "AnonymousCoward");
            plop.setMessage(message);
            plop.setTime(new Date());
            repository.save(plop);
        }
    }
    
    private final SimpleDateFormat legacyNorlogeSdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @RequestMapping(value = "/legacy/xml", produces = "application/xml")
    public LegacyBoard xml() {
        LegacyBoard board = new LegacyBoard();
        List<LegacyPost> legacyPosts = new ArrayList<LegacyPost>();
        Page<Post> posts = repository.findAll(new PageRequest(0, 1000, Sort.Direction.DESC, "time"));
        for(Post post : posts) {
            LegacyPost legacyPost = new LegacyPost();
            legacyPost.setId(post.getTime().getTime());
            legacyPost.setTime(legacyNorlogeSdf.format(post.getTime()));
            legacyPost.setInfo(post.getNickname());
            legacyPost.setMessage(post.getMessage());
            legacyPost.setLogin("");
            legacyPosts.add(legacyPost);
        }
        board.setPost(legacyPosts);
        return board;
    }
}
