package im.bci.jb3.bouchot.gateway;

import im.bci.jb3.bouchot.data.GatewayPostId;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.legacy.LegacyUtils;
import im.bci.jb3.bouchot.logic.CleanUtils;
import im.bci.jb3.bouchot.websocket.WebDirectCoinConnectedMoules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TriggerContext;

import java.io.Reader;
import java.io.StringReader;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public abstract class AbstractTsvBouchotGateway implements Gateway, SchedulableGateway {

    @Autowired
    private WebDirectCoinConnectedMoules connectedMoules;
    @Autowired
    private PostRepository postPepository;
    @Autowired
    protected LegacyUtils legacyUtils;
    private long lastPostId = -1;

    @Value("${jb3.secure}")
    private boolean validateCrapCertificate;

    private final BouchotConfig config;
    private final BouchotAdaptiveRefreshComputer adaptativeRefreshComputer = new BouchotAdaptiveRefreshComputer();

    protected AbstractTsvBouchotGateway(BouchotConfig config) {
        this.config = config;
    }

    protected void importPosts() {
        try {
            Connection connect = Jsoup.connect(config.getGetUrl()).userAgent("jb3")
                    .validateTLSCertificates(validateCrapCertificate || !config.isUsingCrapCertificate());
            if (null != config.getLastIdParameterName()) {
                connect = connect.data(config.getLastIdParameterName(), String.valueOf(lastPostId));
            }
            Connection.Response response = connect.parser(Parser.xmlParser()).method(Connection.Method.GET).execute();
            parsePosts(response);
        } catch (org.jsoup.HttpStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_MODIFIED.value()) {
                adaptativeRefreshComputer.error();
                LogFactory.getLog(this.getClass()).warn("get http error", ex);
            }
        } catch (IOException ex) {
            adaptativeRefreshComputer.error();
            LogFactory.getLog(this.getClass()).error("get error", ex);
        }
    }

    private synchronized void parsePosts(Connection.Response response) throws IOException {
        ArrayList<Post> newPosts = new ArrayList<>();
        try (Reader in = new StringReader(response.body())) {
            TreeMap<Long, CSVRecord> postsToImport = new TreeMap<>();
            for (CSVRecord postToImport : CSVFormat.TDF.parse(in)) {
                postsToImport.put(Long.parseLong(postToImport.get(0)), postToImport);
            }
            for (CSVRecord postToImport : postsToImport.values()) {
                GatewayPostId gatewayPostId = new GatewayPostId();
                gatewayPostId.setGateway(config.getRoom());
                long postId = Long.parseLong(postToImport.get(0));
                gatewayPostId.setPostId(String.valueOf(postId));
                if (!postPepository.existsByGatewayPostId(gatewayPostId)) {
                    Post post = new Post();
                    post.setGatewayPostId(gatewayPostId);
                    post.setRoom(config.getRoom());
                    post.setTime(LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.get(1)));
                    String nickname = decodeTags(postToImport.get(3));
                    if (StringUtils.isBlank(nickname)) {
                        nickname = CleanUtils.truncateNickname(decodeTags(postToImport.get(2)));
                    }
                    post.setNickname(CleanUtils.cleanNickname(nickname));
                    post.setMessage(legacyUtils.convertFromLegacyNorloges(CleanUtils.cleanMessage(CleanUtils.truncateMessage(decodeTags(postToImport.get(4)))), post.getTime(), config.getRoom()));
                    postPepository.save(post);
                    newPosts.add(post);
                }
                if (postId > lastPostId) {
                    lastPostId = postId;
                }
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
            Connection connect = Jsoup.connect(config.getPostUrl()).userAgent("jb3")
                    .data(config.getMessageContentParameterName(),
                            legacyUtils.convertToLegacyNorloges(message,
                                    DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute()
                                    .roundFloorCopy(), getRoom()))
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
            Connection.Response response = connect.parser(Parser.xmlParser()).method(Connection.Method.POST).execute();
            if (config.isUsingXPost()) {
                parsePosts(response);
            } else {
                importPosts();
            }
        } catch (IOException ex) {
            LogFactory.getLog(this.getClass()).error("post error", ex);
        }
    }

    @Override
    public String getRoom() {
        return config.getRoom();
    }

    private String decodeTags(String message) {
        if (config.isTagsEncoded()) {
            return StringEscapeUtils.unescapeXml(message);
        } else {
            return message;
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
