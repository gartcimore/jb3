package im.bci.jb3.bouchot.legacy;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.logic.UserPostHandler;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author devnewton
 */
@Controller
@RequestMapping("/legacy")
public class LegacyController {

	@Autowired
	private UserPostHandler tribune;

	@Autowired
	private PostRepository postPepository;

	@Autowired
	private LegacyUtils legacyUtils;

	@Value("${jb3.defaults.room}")
	private String defaultRoom;

	private Period postsGetPeriod;

	@Value("${jb3.posts.get.period}")
	public void setPostsGetPeriod(String p) {
		postsGetPeriod = ISOPeriodFormat.standard().parsePeriod(p);
	}

	@RequestMapping(path = "/discovery", method = RequestMethod.GET)
	public String discovery(@RequestParam(value = "room", required = false) String room, Model model,
			HttpServletResponse response) {
		model.addAttribute("room", StringUtils.isNotBlank(room) ? room : defaultRoom);
		model.addAttribute("baseurl",
				ServletUriComponentsBuilder.fromCurrentRequest().replacePath("").build().toString());
		response.setContentType("text/xml");
		return "bouchot/legacy/discovery";
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public String post(@RequestParam(value = "nickname", required = false) String nickname,
			@RequestParam(value = "message") String message,
			@RequestParam(value = "room", required = false) String room,
			@RequestParam(value = "last", required = false) Long last,
			@RequestHeader(value = "User-Agent", required = false) String userAgent, WebRequest webRequest,
			@RequestParam(value = "auth", required = false) String auth, Model model, HttpServletResponse response) {
		if (StringUtils.isBlank(nickname)) {
			nickname = userAgent;
		}
		Post post = tribune.post(nickname, legacyUtils.convertFromLegacyNorloges(message, DateTime.now(), room), room,
				auth, ServletUriComponentsBuilder.fromCurrentRequest());
		if (null != post) {
			response.addHeader("X-Post-Id", Long.toString(post.getTime().getMillis()));
		}
		return xml(room, last, webRequest, model, response);
	}

	@RequestMapping(path = "/xml", method = RequestMethod.GET)
	public String xml(@RequestParam(value = "room", required = false) String room,
			@RequestParam(value = "last", required = false) Long lastId, WebRequest webRequest, Model model,
			HttpServletResponse response) {
		if (get(lastId, room, webRequest, model, LegacyUtils.xmlEscaper)) {
			response.setContentType("text/xml");
			return "bouchot/legacy/xml";
		} else {
			return null;
		}
	}

	@RequestMapping(path = "/tsv", method = RequestMethod.GET)
	public String tsv(@RequestParam(value = "room", required = false) String room,
			@RequestParam(value = "last", required = false) Long lastId, WebRequest webRequest, Model model,
			HttpServletResponse response) {
		if (get(lastId, room, webRequest, model, LegacyUtils.csvEscaper)) {
			response.setContentType("text/tab-separated-values");
			response.setHeader("Content-Disposition", "attachment; filename=\"backend.tsv\"");
			return "bouchot/legacy/tsv";
		} else {
			return null;
		}
	}

	private boolean get(Long lastId, String room, WebRequest webRequest, Model model, LegacyUtils.Escaper escaper) {
		DateTime end = DateTime.now(DateTimeZone.UTC).plusHours(1);
		DateTime start = computeStartTime(lastId, end);
		List<Post> posts = postPepository.findPosts(start, end, room);
		if (posts.isEmpty() || webRequest.checkNotModified(posts.get(0).getTime().getMillis())) {
			return false;
		} else {
			LegacyBoard board = new LegacyBoard();
			board.setSite(ServletUriComponentsBuilder.fromCurrentRequest().replacePath("").build().toString());
			board.setTimezone(LegacyUtils.legacyTimezoneId);
			List<LegacyPost> legacyPosts = posts2legacy(posts, escaper);
			board.setPosts(legacyPosts);
			model.addAttribute("board", board);
			return true;
		}
	}

	private List<LegacyPost> posts2legacy(List<Post> posts, LegacyUtils.Escaper escaper) {
		List<LegacyPost> legacyPosts = new ArrayList<>(posts.size());
		for (Post post : posts) {
			legacyPosts.add(legacyUtils.post2legacy(post, escaper));
		}
		return legacyPosts;
	}

	private DateTime computeStartTime(Long lastId, DateTime end) {
		DateTime start;
		if (null != lastId) {
			start = new DateTime(lastId, DateTimeZone.UTC);
			if (start.isAfter(end)) {
				start = end.minusSeconds(1);
			}
		} else {
			start = end.minus(postsGetPeriod);
		}
		// workaround shameful olcc new year bug
		if (start.getYear() < end.getYear()) {
			start = new DateTime(end.getYear(), 1, 1, 0, 0, DateTimeZone.UTC);
		}
		return start;
	}

}
