package im.bci.jb3.controllers;

import im.bci.jb3.data.Fortune;
import im.bci.jb3.data.FortuneRepository;
import im.bci.jb3.frontend.FortuneRQ;
import im.bci.jb3.frontend.PostSearchRQ;
import im.bci.jb3.utils.TimezoneUtils;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fortune")
public class FortuneController {

    @Autowired
    private FortuneRepository fortuneRepository;

    @RequestMapping("")
    public String index(PostSearchRQ rq, Model model) {
        model.addAttribute("timezone", TimezoneUtils.javascriptTimezoneOffsetToJavaTimeZoneId(rq.getTimezoneOffset()));
        model.addAttribute("rq", rq);
        model.addAttribute("rq", rq);
        model.addAttribute("fortunes", fortuneRepository.search(rq));
        return "fortune/index";
    }

    @RequestMapping("/{fortuneId}")
    public String view(@PathVariable("fortuneId") String fortuneId, FortuneRQ rq, Model model) {
        model.addAttribute("timezone", TimezoneUtils.javascriptTimezoneOffsetToJavaTimeZoneId(rq.getTimezoneOffset()));
        Fortune fortune = fortuneRepository.findOne(fortuneId);
        model.addAttribute("fortune", fortune);
        return "fortune/view";
    }
}
