package im.bci.jb3.controllers;

import im.bci.jb3.backend.legacy.LegacyBoard;
import im.bci.jb3.backend.legacy.LegacyPost;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.Norloge;
import static im.bci.jb3.logic.Norloge.parseNorloge;
import im.bci.jb3.logic.TribuneService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author devnewton
 */
@RestController
@RequestMapping("/legacy")
public class LegacyRestController {

    @Autowired
    private TribuneService tribune;

    @Autowired
    private PostRepository postPepository;

    @RequestMapping("/post")
    public void post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message, @RequestHeader(value="User-Agent", required = false) String userAgent) {
        if(StringUtils.isBlank(nickname)) {
            nickname = userAgent;
        }
        tribune.post(nickname, message);
    }
    
    private static final DateTimeFormatter legacyPostTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZoneUTC();

    @RequestMapping(value = "/xml", produces = { "application/xml", "text/xml" } )
    public LegacyBoard xml(WebRequest webRequest) {
        List<Post> posts = tribune.get();
        if (posts.isEmpty() || webRequest.checkNotModified(posts.get(0).getTime().getTime())) {
            return null;
        } else {
            LegacyBoard board = new LegacyBoard();
            List<LegacyPost> legacyPosts = new ArrayList<LegacyPost>();
            for (Post post : posts) {
                LegacyPost legacyPost = new LegacyPost();
                final long time = post.getTime().getTime();
                legacyPost.setId(time);
                legacyPost.setTime(legacyPostTimeFormatter.print(time));
                legacyPost.setInfo(post.getNickname());
                legacyPost.setMessage(convertNonLegacyNorloges(post.getMessage()));
                legacyPost.setLogin("");
                legacyPosts.add(legacyPost);
            }
            board.setPost(legacyPosts);
            return board;
        }
    }

    private String convertNonLegacyNorloges(String message) {
        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            String item = scanner.next();
            Norloge norloge = parseNorloge(item);
            if (null != norloge) {
                if (null == norloge.getTime() && null != norloge.getId() && null == norloge.getBouchot()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        message = message.substring(0, scanner.match().start()) + new Norloge().withTime(post.getTime()) + message.substring(scanner.match().end());
                    }
                }
            }
        }
        return message;
    }
}
