package im.bci.jb3.bouchot.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import im.bci.jb3.bouchot.legacy.LegacyUtils;
import im.bci.jb3.dlfp.DlfpOauthToken;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class DlfpGateway extends AbstractXmlBouchotGateway {

    @Autowired
    private ObjectMapper objectMapper;

    private static BouchotConfig createConf() {
        BouchotConfig conf = new BouchotConfig();
        conf.setRoom("dlfp");
        conf.setGetUrl("https://linuxfr.org/board/index.xml");
        conf.setPostUrl("https://linuxfr.org/board");
        conf.setCookieName("linuxfr.org_session");
        conf.setReferrer("https://linuxfr.org/board");
        conf.setMessageContentParameterName("board[message]");
        return conf;
    }

    public DlfpGateway() {
        super(createConf());
    }

    @Override
    public void post(String nickname, String message, String auth) {
        try {
            DlfpOauthToken token = objectMapper.readValue(auth, DlfpOauthToken.class);
            Jsoup.connect("https://linuxfr.org/api/v1/board").userAgent("jb3")
                    .data("bearer_token", token.getAccess_token())
                    .data("message",
                            legacyUtils.convertToLegacyNorloges(message,
                                    DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute()
                                    .roundFloorCopy(), getRoom()))
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute();
            importPosts();
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).error("post error", ex);
        }
    }

}
