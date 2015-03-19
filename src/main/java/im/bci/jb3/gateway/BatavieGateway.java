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
import org.jsoup.Connection;
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
public class BatavieGateway implements Gateway {

    @Autowired
    private PostRepository postPepository;

    @Autowired
    private LegacyUtils legacyUtils;

    private static final String BOUCHOT_NAME = "batavie";

    @Scheduled(cron = "0/30 * * * * *")
    public void importPosts() {
        try {
            Document doc = Jsoup.connect("http://batavie.leguyader.eu/remote.xml").parser(Parser.xmlParser()).get();
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
                    post.setMessage(legacyUtils.convertFromLegacyNorloges(BOUCHOT_NAME, CleanUtils.cleanMessage(replaceUrls(postToImport.select("message").html()))));
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
            }
        } catch (IOException ex) {
            Logger.getLogger(BatavieGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void post(String nickname, String message) {
        try {
            Jsoup.connect("http://batavie.leguyader.eu/index.php/add").data("message", legacyUtils.convertToLegacyNorloges(message, DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy())).userAgent(nickname).parser(Parser.xmlParser()).method(Connection.Method.POST).post();
        } catch (IOException ex) {
            Logger.getLogger(BatavieGateway.class.getName()).log(Level.SEVERE, null, ex);
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
