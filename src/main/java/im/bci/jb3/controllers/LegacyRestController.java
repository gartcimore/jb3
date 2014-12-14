package im.bci.jb3.controllers;

import im.bci.jb3.backend.legacy.LegacyBoard;
import im.bci.jb3.backend.legacy.LegacyPost;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.logic.Norloge;
import static im.bci.jb3.logic.Norloge.parseNorloge;
import im.bci.jb3.logic.TribuneService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
                legacyPost.setMessage(convertToLegacyNorloges(post.getMessage()));
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
        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            String item = scanner.next();
            Norloge norloge = parseNorloge(item);
            if (null != norloge && null == norloge.getBouchot()) {
                if (null != norloge.getTime()) {
                    int size = scanner.match().end() - scanner.match().start();
                    DateTimeFormatter formatter;
                    switch (size) {
                        case 5:
                            formatter = toLegacyShortNorlogeFormatter;
                            break;
                        case 8:
                            formatter = toLegacyNormalNorlogeFormatter;
                            break;
                        case 14:
                            formatter = toLegacyLongNorlogeFormatter;
                            break;
                        default:
                            formatter = toLegacyFullNorlogeFormatter;
                            break;
                    }
                    message = message.substring(0, scanner.match().start()) + formatter.print(norloge.getTime()) + message.substring(scanner.match().end());
                } else if (null != norloge.getId()) {
                    Post post = postPepository.findOne(norloge.getId());
                    if (null != post) {
                        message = message.substring(0, scanner.match().start()) + new Norloge().withTime(post.getTime()) + message.substring(scanner.match().end());
                    }
                }
            }
        }
        return message;
    }

    private static final DateTimeFormatter fromLegacyFullNorlogeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyLongNorlogeFormatter = DateTimeFormat.forPattern("MM/dd#HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyNormalNorlogeFormatter = DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC();
    private static final DateTimeFormatter fromLegacyShortNorlogeFormatter = DateTimeFormat.forPattern("HH:mm").withZoneUTC();

    private String convertFromLegacyNorloges(String message) {
        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            String item = scanner.next();
            Norloge norloge = parseNorloge(item);
            if (null != norloge && null == norloge.getBouchot()) {
                if (null != norloge.getTime()) {
                    int size = scanner.match().end() - scanner.match().start();
                    DateTimeFormatter formatter;
                    switch (size) {
                        case 5:
                            formatter = fromLegacyShortNorlogeFormatter;
                            break;
                        case 8:
                            formatter = fromLegacyNormalNorlogeFormatter;
                            break;
                        case 14:
                            formatter = fromLegacyLongNorlogeFormatter;
                            break;
                        default:
                            formatter = fromLegacyFullNorlogeFormatter;
                            break;
                    }
                    message = message.substring(0, scanner.match().start()) + formatter.print(norloge.getTime().minusHours(1)) + message.substring(scanner.match().end());
                }
            }
        }
        return message;
    }

}
