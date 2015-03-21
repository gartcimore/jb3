package im.bci.jb3.gateway;

import im.bci.jb3.data.GatewayPostId;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.legacy.LegacyUtils;
import im.bci.jb3.logic.CleanUtils;
import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public abstract class AbstractBouchotGateway implements Gateway {

    @Autowired
    private PostRepository postPepository;
    @Autowired
    private LegacyUtils legacyUtils;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private long lastPostId = -1;

    private final String room = "euromussels";
    private final String getUrl = "http://euromussels.eu/?q=tribune.xml";
    private final String postUrl = "http://euromussels.eu/?q=tribune/post";
    private final String lastIdParameterName = "last_id";
    private final String messageContentParameterName= "message";

    public synchronized void importPosts() {
        try {
            Document doc = Jsoup.connect(getUrl).data(lastIdParameterName, String.valueOf(lastPostId)).parser(Parser.xmlParser()).get();
            Elements postsToImport = doc.select("post");
            ArrayList<Post> newPosts = new ArrayList<Post>();
            for (ListIterator<Element> iterator = postsToImport.listIterator(postsToImport.size()); iterator.hasPrevious();) {
                Element postToImport = iterator.previous();
                GatewayPostId gatewayPostId = new GatewayPostId();
                gatewayPostId.setGateway(room);
                long postId = Long.parseLong(postToImport.attr("id"));
                gatewayPostId.setPostId(String.valueOf(postId));
                if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                    Post post = new Post();
                    post.setGatewayPostId(gatewayPostId);
                    post.setMessage(legacyUtils.convertFromLegacyNorloges(room, CleanUtils.cleanMessage(replaceUrls(StringEscapeUtils.unescapeXml(postToImport.select("message").text())))));
                    String nickname = StringEscapeUtils.unescapeXml(postToImport.select("login").text());
                    if (StringUtils.isBlank(nickname)) {
                        nickname = StringEscapeUtils.unescapeXml(postToImport.select("info").text());
                        if (StringUtils.isBlank(nickname)) {
                            nickname = "AnonymousMussels";
                        }
                    }
                    post.setNickname(CleanUtils.cleanNickname(nickname));
                    post.setRoom(room);
                    post.setTime(LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.attr("time")));
                    postPepository.save(post);
                    newPosts.add(post);
                }
                if (postId > lastPostId) {
                    lastPostId = postId;
                }
            }
            if (!newPosts.isEmpty()) {
                simpMessagingTemplate.convertAndSend("/topic/posts", newPosts);
            }
        } catch (IOException ex) {
            Logger.getLogger(EuromusselsGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void post(String nickname, String message) {
        try {
            Jsoup.connect(postUrl).data(messageContentParameterName, legacyUtils.convertToLegacyNorloges(message, DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy())).userAgent(nickname).data(lastIdParameterName, String.valueOf(lastPostId)).parser(Parser.xmlParser()).post();
            importPosts();
        } catch (IOException ex) {
            Logger.getLogger(EuromusselsGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getRoom() {
        return room;
    }

    private String replaceUrls(String message) {
        Document doc = Jsoup.parse(message);
        for (Element a : doc.select("a")) {
            a.text(a.attr("href"));
        }
        return doc.toString();
    }

}
