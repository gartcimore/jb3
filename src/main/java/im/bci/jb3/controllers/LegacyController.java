package im.bci.jb3.controllers;

import im.bci.jb3.backend.legacy.LegacyBoard;
import im.bci.jb3.backend.legacy.LegacyPost;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.Norloge;
import im.bci.jb3.logic.TribuneService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author devnewton
 */
@Controller
@RequestMapping("/legacy")
public class LegacyController {

    @Value("${jb3.host}")
    private String site;

    @Autowired
    private TribuneService tribune;

    @Autowired
    private PostRepository postPepository;

    @RequestMapping("/post")
    public void post(@RequestParam(value = "nickname", required = false) String nickname, @RequestParam(value = "message") String message, @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        if (StringUtils.isBlank(nickname)) {
            nickname = userAgent;
        }
        tribune.post(nickname, convertFromLegacyNorloges(message));
    }

    private static final String legacyTimezoneId = "Europe/Paris";
    private static final DateTimeZone legacyTimeZone = DateTimeZone.forID(legacyTimezoneId);
    private static final DateTimeFormatter legacyPostTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZone(legacyTimeZone);

    @RequestMapping(value = "/xml")
    public String xml(WebRequest webRequest, Model model, HttpServletResponse response) {
        DateTime end = DateTime.now(DateTimeZone.UTC);
        DateTime start = end.minusWeeks(1);
        
        //workaround shameful olcc new year bug
        if(start.getYear() < end.getYear()) {
            start = new DateTime(end.getYear(), 1, 1, 0, 0, DateTimeZone.UTC);
        }

        List<Post> posts = postPepository.findPosts(start, end);
        if (posts.isEmpty() || webRequest.checkNotModified(posts.get(0).getTime().getMillis())) {
            return null;
        } else {
            LegacyBoard board = new LegacyBoard();
            board.setSite(site);
            board.setTimezone(legacyTimezoneId);
            List<LegacyPost> legacyPosts = new ArrayList<LegacyPost>(posts.size());
            for (Post post : posts) {
                LegacyPost legacyPost = new LegacyPost();
                    legacyPost.setId(post.getTime().getMillis());
                    legacyPost.setTime(legacyPostTimeFormatter.print(post.getTime()));
                    legacyPost.setInfo(StringEscapeUtils.escapeXml10(Jsoup.clean(post.getNickname(), Whitelist.none())));
                    legacyPost.setMessage(StringEscapeUtils.escapeXml10(Jsoup.clean(convertToLegacyNorloges(convertUrls(post.getMessage()), post.getTime()), messageWhitelist)));
                    legacyPosts.add(legacyPost);
            }
            board.setPosts(legacyPosts);
            model.addAttribute("board", board);
            response.setContentType("text/xml");
            return "legacy/xml";
        }
    }

    private static final DateTimeFormatter toLegacyFullNorlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZone(legacyTimeZone);
    private static final DateTimeFormatter toLegacyLongNorlogeFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZone(legacyTimeZone);
    private static final DateTimeFormatter toLegacyNormalNorlogeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(legacyTimeZone);
    private static final DateTimeFormatter toLegacyShortNorlogeFormatter = DateTimeFormat.forPattern("HH:mm").withZone(legacyTimeZone);

    private String convertToLegacyNorloges(String message, final DateTime postTime) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                if (null != norloge.getId()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        final DateTime referencedPostTime = new DateTime(post.getTime());
                        DateTimeFormatter formatter = findLegacyNorlogeFormatter(postTime, referencedPostTime);
                        matcher.appendReplacement(sb, formatter.print(referencedPostTime));
                        return;
                    }
                }
                matcher.appendReplacement(sb, norloge.toString());
            }

            @Override
            public void end(Matcher matcher) {
                matcher.appendTail(sb);
            }

        });
        return sb.toString();
    }

    private DateTimeFormatter findLegacyNorlogeFormatter(DateTime postTime, DateTime referencedPostTime) {
        if (Days.daysBetween(postTime, referencedPostTime).isLessThan(Days.ONE)) {
            if (referencedPostTime.getSecondOfMinute() == 0) {
                return toLegacyShortNorlogeFormatter;
            } else {
                return toLegacyNormalNorlogeFormatter;
            }
        } else if (Years.yearsBetween(postTime, referencedPostTime).isLessThan(Years.ONE)) {
            return toLegacyLongNorlogeFormatter;
        } else {
            return toLegacyFullNorlogeFormatter;
        }
    }

    private String convertFromLegacyNorloges(String message) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                DateTime time = norloge.getTime();
                if (null != time) {
                    time = time.withZoneRetainFields(legacyTimeZone);
                    Post post = postPepository.findOne(time, time.plusSeconds(1));
                    if (null != post) {
                        matcher.appendReplacement(sb, Norloge.format(post));
                        return;
                    }
                }
                matcher.appendReplacement(sb, norloge.toString());
            }

            @Override
            public void end(Matcher matcher) {
                matcher.appendTail(sb);
            }
        });
        return sb.toString();
    }

    private static final Pattern urlPattern = Pattern.compile("(https?|ftp|gopher)://[^\\s]+");

    private final Whitelist messageWhitelist = Whitelist.none().addTags("b", "i", "s", "u", "tt", "a").addAttributes("a", "href", "rel", "target");

    private String convertUrls(String message) {
        Matcher matcher = urlPattern.matcher(message);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<a href=\"$0\" rel=\"nofollow\" target=\"_blank\">[url]</a>");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
