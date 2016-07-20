package im.bci.jb3.archives;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.coincoin.PostSearchRQ;

@Controller
@RequestMapping("/archives")
public class ArchivesController {
    @Autowired
    private PostRepository postRepository;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model, PostSearchRQ rq) {
        model.addAttribute("wro-group", "archives");
        model.addAttribute("rq", rq);
        model.addAttribute("posts", searchPosts(rq));
        return "archives/archives";
    }

    private List<Post> searchPosts(PostSearchRQ rq) {
        if(null != rq) {
            rq.setPageSize(10000);
            return postRepository.search(rq);
        }
        return Collections.emptyList();
    }
}
