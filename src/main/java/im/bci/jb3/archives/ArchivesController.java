package im.bci.jb3.archives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.coincoin.PostSearchRQ;
import im.bci.jb3.coincoin.PostSearchResultMV;

@Controller
@RequestMapping("/archives")
public class ArchivesController {
    @Autowired
    private PostRepository postRepository;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model, PostSearchRQ rq) {
        model.addAttribute("wro-group", "archives");
        model.addAttribute("rq", rq);
        model.addAttribute("postsMV", searchPosts(rq));
        return "archives/archives";
    }

    private PostSearchResultMV searchPosts(PostSearchRQ rq) {
        PostSearchResultMV mv = new PostSearchResultMV();
        if(null != rq) {
            mv.setPosts(postRepository.search(rq));
        }
        mv.setHasPrevious(rq.getPage() > 0);
        mv.setHasNext(mv.getPosts().size() >= rq.getPageSize());
        return mv;
    }
}
