package im.bci.jb3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontendController {

    @RequestMapping("")
    public String index() {
        return "index";
    }
    
    @RequestMapping("/rooms")
    public String rooms() {
        return "rooms";
    }
    
    @RequestMapping("error")
    public String error() {
        return "error";
    }
}
