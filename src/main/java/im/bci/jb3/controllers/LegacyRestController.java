package im.bci.jb3.controllers;

import im.bci.jb3.backend.legacy.LegacyBoard;
import im.bci.jb3.backend.legacy.LegacyPost;
import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Tribune;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@RequestMapping("/legacy")
public class LegacyRestController {

    @Autowired
    private Tribune tribune;

    @RequestMapping("/post")
    public void post(@RequestParam(value = "nickname") String nickname, @RequestParam(value = "message") String message) {
        tribune.post(nickname, message);
    }
    
    private final SimpleDateFormat legacyNorlogeSdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @RequestMapping(value = "/xml", produces = "application/xml")
    public LegacyBoard xml() {
        LegacyBoard board = new LegacyBoard();
        List<LegacyPost> legacyPosts = new ArrayList<LegacyPost>();
        List<Post> posts = tribune.get();
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
