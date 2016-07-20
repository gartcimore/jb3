package im.bci.jb3.archives;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    public String index(Model model, ArchivesRQ rq) {
        model.addAttribute("wro-group", "archives");
        model.addAttribute("rq", rq);
        model.addAttribute("posts", searchPosts(rq));
        return "archives/archives";
    }

    private List<Post> searchPosts(ArchivesRQ archiveRq) {
        if(null != archiveRq) {
            PostSearchRQ searchRq = new PostSearchRQ();
            if(!StringUtils.isEmpty(archiveRq.getDate())) {
                searchRq.setFrom(new DateTime(archiveRq.getDate()).withTimeAtStartOfDay().getMillis());
                searchRq.setTo(new DateTime(archiveRq.getDate()).plusDays(1).withTimeAtStartOfDay().getMillis());
            }
            if(!StringUtils.isEmpty(archiveRq.getRoom())) {
                searchRq.setRoomFilter(archiveRq.getRoom());
            }
            searchRq.setPageSize(10000);
            return postRepository.search(searchRq);
        }
        return Collections.emptyList();
    }
}
