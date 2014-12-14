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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author devnewton
 */
@RestController
@RequestMapping("/legacy")
public class LegacyRestController {

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

    private static final String legacyTimezone = "Europe/Paris";
    private static final DateTimeFormatter legacyPostTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZone(DateTimeZone.forID(legacyTimezone));

    @RequestMapping(value = "/xml", produces = {"application/xml", "text/xml"})
    public LegacyBoard xml(WebRequest webRequest) {
        List<Post> posts = tribune.get();
        if (posts.isEmpty() || webRequest.checkNotModified(posts.get(0).getTime().getTime())) {
            return null;
        } else {
            LegacyBoard board = new LegacyBoard();
            board.setSite(site);
            board.setTimezone(legacyTimezone);
            List<LegacyPost> legacyPosts = new ArrayList<LegacyPost>();
            for (Post post : posts) {
                LegacyPost legacyPost = new LegacyPost();
                final long time = post.getTime().getTime();
                legacyPost.setId(time);
                legacyPost.setTime(legacyPostTimeFormatter.print(time));
                legacyPost.setInfo(post.getNickname());
                legacyPost.setMessage(convertToLegacyNorloges(convertUrls(post.getMessage())));
                legacyPost.setLogin("");
                legacyPosts.add(legacyPost);
            }
            board.setPost(legacyPosts);
            return board;
        }
    }

    private static final DateTimeFormatter toLegacyFullNorlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZone(DateTimeZone.forID(legacyTimezone));
    private static final DateTimeFormatter toLegacyLongNorlogeFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZone(DateTimeZone.forID(legacyTimezone));
    private static final DateTimeFormatter toLegacyNormalNorlogeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(DateTimeZone.forID(legacyTimezone));
    private static final DateTimeFormatter toLegacyShortNorlogeFormatter = DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.forID(legacyTimezone));

    private String convertToLegacyNorloges(String message) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                if (null != norloge.getTime()) {
                    if (null != matcher.group("year")) {
                        matcher.appendReplacement(sb, toLegacyFullNorlogeFormatter.print(norloge.getTime()));
                    } else if (null != matcher.group("date")) {
                        matcher.appendReplacement(sb, toLegacyLongNorlogeFormatter.print(norloge.getTime()));
                    } else if (null == matcher.group("seconds")) {
                        matcher.appendReplacement(sb, toLegacyShortNorlogeFormatter.print(norloge.getTime()));
                    } else {
                        matcher.appendReplacement(sb, toLegacyNormalNorlogeFormatter.print(norloge.getTime()));
                    }
                } else if (null != norloge.getId()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        matcher.appendReplacement(sb, toLegacyFullNorlogeFormatter.print(new DateTime(post.getTime())));
                    } else {
                        matcher.appendReplacement(sb, norloge.toString());
                    }
                } else {
                    matcher.appendReplacement(sb, norloge.toString());
                }
            }

            @Override
            public void end(Matcher matcher) {
                matcher.appendTail(sb);
            }
        });
        return sb.toString();
    }

    private static final DateTimeFormatter fromLegacyFullNorlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyLongNorlogeFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyNormalNorlogeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyShortNorlogeFormatter = DateTimeFormat.forPattern("HH:mm").withZoneUTC();

    private String convertFromLegacyNorloges(String message) {
        final StringBuffer sb = new StringBuffer();
        Norloge.forEachNorloge(message, new Norloge.NorlogeProcessor() {

            @Override
            public void process(Norloge norloge, Matcher matcher) {
                DateTime time = norloge.getTime();
                if (null != time) {
                    time = time.minusHours(1);
                    if (null != matcher.group("year")) {
                        matcher.appendReplacement(sb, fromLegacyFullNorlogeFormatter.print(time));
                    } else if (null != matcher.group("date")) {
                        matcher.appendReplacement(sb, fromLegacyLongNorlogeFormatter.print(time));
                    } else if (null == matcher.group("seconds")) {
                        matcher.appendReplacement(sb, fromLegacyShortNorlogeFormatter.print(time));
                    } else {
                        matcher.appendReplacement(sb, fromLegacyNormalNorlogeFormatter.print(time));
                    }
                } else {
                    matcher.appendReplacement(sb, norloge.toString());
                }
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
        return Jsoup.clean(sb.toString(), messageWhitelist);
    }

}
