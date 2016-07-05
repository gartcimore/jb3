package im.bci.jb3.gateway;

import im.bci.jb3.data.GatewayPostId;
import im.bci.jb3.data.Post;
import im.bci.jb3.data.PostRepository;
import im.bci.jb3.legacy.LegacyUtils;
import im.bci.jb3.logic.CleanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TriggerContext;
import im.bci.jb3.websocket.WebDirectCoinConnectedMoules;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public abstract class AbstractBouchotGateway implements Gateway, SchedulableGateway {

    @Autowired
    private WebDirectCoinConnectedMoules connectedMoules;
    @Autowired
    private PostRepository postPepository;
    @Autowired
    private LegacyUtils legacyUtils;
    private long lastPostId = -1;

    @Value("${jb3.secure}")
    private boolean validateCrapCertificate;

    private final BouchotConfig config;
    private final BouchotAdaptiveRefreshComputer adaptativeRefreshComputer = new BouchotAdaptiveRefreshComputer();

    protected AbstractBouchotGateway(BouchotConfig config) {
        this.config = config;
    }

    private void importPosts() {
        try {
            Connection connect = Jsoup.connect(config.getGetUrl())
                    .validateTLSCertificates(validateCrapCertificate || !config.isUsingCrapCertificate());
            if (null != config.getLastIdParameterName()) {
                connect = connect.data(config.getLastIdParameterName(), String.valueOf(lastPostId));
            }
            Document doc = connect.parser(Parser.xmlParser()).get();
            parsePosts(doc);
        } catch (org.jsoup.HttpStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_MODIFIED.value()) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void parsePosts(Document doc) {
        System.out.println(DateTime.now() + " " + getClass().getName() + " refresh");
        Elements postsToImport = doc.select("post");
        ArrayList<Post> newPosts = new ArrayList<Post>();
        for (ListIterator<Element> iterator = postsToImport.listIterator(postsToImport.size()); iterator
                .hasPrevious();) {
            Element postToImport = iterator.previous();
            GatewayPostId gatewayPostId = new GatewayPostId();
            gatewayPostId.setGateway(config.getRoom());
            long postId = Long.parseLong(postToImport.attr("id"));
            gatewayPostId.setPostId(String.valueOf(postId));
            if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                Post post = new Post();
                post.setGatewayPostId(gatewayPostId);
                post.setMessage(legacyUtils.convertFromLegacyNorloges(config.getRoom(),
                        CleanUtils.cleanMessage(CleanUtils.truncateMessage(decodeTags(postToImport.select("message").first())))));
                String nickname = decodeTags(postToImport.select("login").first());
                if (StringUtils.isBlank(nickname)) {
                    nickname = CleanUtils.truncateNickname(decodeTags(postToImport.select("info").first()));
                }
                post.setNickname(CleanUtils.cleanNickname(nickname));
                post.setRoom(config.getRoom());
                post.setTime(LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.attr("time")));
                postPepository.save(post);
                newPosts.add(post);
            }
            if (postId > lastPostId) {
                lastPostId = postId;
            }
        }
        adaptativeRefreshComputer.analyseBouchotPostsResponse(newPosts);
        if (!newPosts.isEmpty()) {
            connectedMoules.send(newPosts);
        }
    }

    @Override
    public void post(String nickname, String message, String auth) {
        try {
            Connection connect = Jsoup.connect(config.getPostUrl())
                    .data(config.getMessageContentParameterName(),
                            legacyUtils.convertToLegacyNorloges(message,
                                    DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute()
                                    .roundFloorCopy()))
                    .userAgent(nickname)
                    .validateTLSCertificates(validateCrapCertificate || !config.isUsingCrapCertificate());
            if (null != config.getCookieName()) {
                connect = connect.cookie(config.getCookieName(), auth);
            }
            if (null != config.getReferrer()) {
                connect = connect.referrer(config.getReferrer());
            }
            if (null != config.getLastIdParameterName()) {
                connect = connect.data(config.getLastIdParameterName(), String.valueOf(lastPostId));
            }
            Document doc = connect.parser(Parser.xmlParser()).post();
            if (config.isUsingXPost()) {
                parsePosts(doc);
            } else {
                importPosts();
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getRoom() {
        return config.getRoom();
    }

    private String decodeTags(Element message) {
        if (config.isTagsEncoded()) {
            return StringEscapeUtils.unescapeXml(message.text());
        } else {
            return message.html();
        }
    }

    @Override
    public Date nextExecutionTime(TriggerContext tc) {
        return adaptativeRefreshComputer.nextRefreshDate();
    }

    @Override
    public void run() {
        importPosts();
    }

}
