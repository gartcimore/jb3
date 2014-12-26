package im.bci.jb3.controllers;

import im.bci.jb3.data.Fortune;
import im.bci.jb3.data.FortuneRepository;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.frontend.FortuneSearchFO;
import im.bci.jb3.frontend.PostSearchRQ;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/archive")
public class ArchiveController {
    
    @Autowired
    private PostRepository postRepository;

    @RequestMapping("")
    public String index(PostSearchRQ rq, Model model) {
        model.addAttribute("rq", rq);
        if(StringUtils.isNotBlank(rq.getContent())) {
            model.addAttribute("posts", postRepository.search(rq));
        }
        return "archive/index";
    }
}
