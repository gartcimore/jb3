package im.bci.jb3.gateway;

import im.bci.jb3.data.GatewayPostId;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.legacy.LegacyUtils;
import im.bci.jb3.logic.CleanUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class EuromusselsGateway implements Gateway {

    @Autowired
    private PostRepository postPepository;

    @Autowired
    private LegacyUtils legacyUtils;

    private long lastPostId = -1;
    private static final String BOUCHOT_NAME = "euromussels";

    @Scheduled(cron = "0/30 * * * * *")
    public void importPosts() {
        try {
            Document doc = Jsoup.connect("http://euromussels.eu").data("q", "tribune.xml").data("last_id", String.valueOf(lastPostId)).parser(Parser.xmlParser()).get();
            for (Element postToImport : doc.select("post")) {
                GatewayPostId gatewayPostId = new GatewayPostId();
                gatewayPostId.setGateway(BOUCHOT_NAME);
                long postId = Long.parseLong(postToImport.attr("id"));
                gatewayPostId.setPostId(String.valueOf(postId));
                if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                    Post post = new Post();
                    post.setGatewayPostId(gatewayPostId);
                    post.setMessage(legacyUtils.convertFromLegacyNorloges(CleanUtils.cleanMessage(replaceUrls(StringEscapeUtils.unescapeXml(postToImport.select("message").text())))));
                    String nickname = StringEscapeUtils.unescapeXml(postToImport.select("login").text());
                    if (StringUtils.isBlank(nickname)) {
                        nickname = StringEscapeUtils.unescapeXml(postToImport.select("info").text());
                        if (StringUtils.isBlank(nickname)) {
                            nickname = "AnonymousMussels";
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
            Logger.getLogger(EuromusselsGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void post(String nickname, String message) {
        try {
            Jsoup.connect("http://euromussels.eu/?q=tribune/post").data("message", legacyUtils.convertToLegacyNorloges(message, DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy())).userAgent(nickname).data("last_id", String.valueOf(lastPostId)).parser(Parser.xmlParser()).post();
        } catch (IOException ex) {
            Logger.getLogger(EuromusselsGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getRoom() {
        return BOUCHOT_NAME;
    }

    private String replaceUrls(String message) {
        Document doc = Jsoup.parse(message);
        for(Element a : doc.select("a")) {
            a.text(a.attr("href"));
        }
        return doc.toString();
    }

}