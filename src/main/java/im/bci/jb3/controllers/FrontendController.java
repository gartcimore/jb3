package im.bci.jb3.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontendController implements ErrorController {

    @Value("${jb3.room.default}")
    private String defaultRoom;

    @RequestMapping("")
    public String index(Model model) {
        model.addAttribute("jb3DefaultRoom", defaultRoom);
        return "index";
    }

    @RequestMapping("/rooms")
    public String rooms() {
        return "rooms";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
