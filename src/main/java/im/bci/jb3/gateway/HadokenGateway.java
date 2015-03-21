package im.bci.jb3.gateway;

import im.bci.jb3.data.GatewayPostId;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.legacy.LegacyUtils;
import im.bci.jb3.logic.CleanUtils;
import java.io.IOException;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class HadokenGateway implements Gateway {

    @Autowired
    private PostRepository postPepository;

    @Autowired
    private LegacyUtils legacyUtils;

    private long lastPostId = -1;
    private static final String BOUCHOT_NAME = "hadoken";

    @Scheduled(cron = "0/30 * * * * *")
    public synchronized void importPosts() {
        try {
            Document doc = Jsoup.connect("http://hadoken.free.fr/board/remote.php").data("id", String.valueOf(lastPostId)).parser(Parser.xmlParser()).get();
            Elements postsToImport = doc.select("post");
            for (ListIterator<Element> iterator = postsToImport.listIterator(postsToImport.size()); iterator.hasPrevious();) {
                Element postToImport = iterator.previous();
                GatewayPostId gatewayPostId = new GatewayPostId();
                gatewayPostId.setGateway(BOUCHOT_NAME);
                long postId = Long.parseLong(postToImport.attr("id"));
                gatewayPostId.setPostId(String.valueOf(postId));
                if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                    Post post = new Post();
                    post.setGatewayPostId(gatewayPostId);
                    post.setMessage(legacyUtils.convertFromLegacyNorloges(BOUCHOT_NAME, CleanUtils.cleanMessage(replaceUrls(StringEscapeUtils.unescapeXml(postToImport.select("message").text())))));
                    String nickname = StringEscapeUtils.unescapeXml(postToImport.select("login").text());
                    if (StringUtils.isBlank(nickname)) {
                        nickname = StringEscapeUtils.unescapeXml(postToImport.select("info").text());
                        if (StringUtils.isBlank(nickname)) {
                            nickname = "Moulanonyme";
                        }
                    }
                    post.setNickname(CleanUtils.cleanNickname(nickname));
                    post.setRoom(BOUCHOT_NAME);
                    post.setTime(LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.attr("time")));
                    postPepository.save(post);
                }
                if (postId > lastPostId) {
                    lastPostId = postId;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HadokenGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void post(String nickname, String message) {
        try {
            Jsoup.connect("http://hadoken.free.fr/board/post.php").data("message", legacyUtils.convertToLegacyNorloges(message, DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy())).userAgent(nickname).parser(Parser.xmlParser()).post();
            importPosts();
        } catch (IOException ex) {
            Logger.getLogger(HadokenGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getRoom() {
        return BOUCHOT_NAME;
    }

    private String replaceUrls(String message) {
        Document doc = Jsoup.parse(message);
        for (Element a : doc.select("a")) {
            a.text(a.attr("href"));
        }
        return doc.toString();
    }

}
