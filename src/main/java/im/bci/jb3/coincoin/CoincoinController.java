package im.bci.jb3.coincoin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class CoincoinController {

	@Value("${jb3.room.default}")
	private String defaultRoom;

	@RequestMapping(path = "", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("jb3DefaultRoom", defaultRoom);
		model.addAttribute("wro-group", "coincoin");
		return "coincoin/coincoin";
	}

	@RequestMapping(path = "/rooms", method = RequestMethod.GET)
	public String rooms(Model model) {
		model.addAttribute("wro-group", "rooms");
		return "rooms/rooms";
	}
	
	@RequestMapping(path = "/paste", method = RequestMethod.GET)
	public String paste(Model model) {
		model.addAttribute("wro-group", "paste");
		return "paste/paste";
	}
        
	@RequestMapping(path = "/visio", method = RequestMethod.GET)
	public String visio(Model model, @RequestParam(name="room", required = false) String room) {
                model.addAttribute("jb3VisioDefaultRoom", StringUtils.isEmpty(room) ? this.defaultRoom : room);
		model.addAttribute("wro-group", "visio");
		return "visio/visio";
	}
}
