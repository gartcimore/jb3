package im.bci.jb3.fortune;

import im.bci.jb3.data.PostRepository;
import im.bci.jb3.frontend.PostSearchRQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/archive")
public class ArchiveController {

    @Autowired
    private PostRepository postRepository;

    @RequestMapping(path="", method = RequestMethod.GET)
    public String index(PostSearchRQ rq, Model model) {
        model.addAttribute("rq", rq);
        model.addAttribute("posts", postRepository.search(rq));
        return "archive/index";
    }
}
