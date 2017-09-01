package im.bci.jb3.bouchot.gateway;

import im.bci.jb3.bouchot.legacy.LegacyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Request.Builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class AnatidGateway implements Gateway {

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    protected LegacyUtils legacyUtils;

    private HttpUrl plopToUrl;
    private List<String> rooms;
    private final BouchotPostCallBack bouchotPostCallback = new BouchotPostCallBack();

    @Value("${jb3.anatid.plopto.url:}")
    public void setPlopToUrl(String plopToUrl) {
        if (StringUtils.isNotBlank(plopToUrl)) {
            this.plopToUrl = HttpUrl.parse(plopToUrl);
        }
    }

    @Value("${jb3.anatid.rooms:}")
    public void setRooms(String rooms) {
        this.rooms = Arrays.asList(StringUtils.split(rooms, ','));
    }

    @Override
    public boolean handlePost(String nickname, String message, String room, String auth) {
        if (rooms.contains(room)) {
            if (null != plopToUrl) {
                okhttp3.FormBody.Builder body = new FormBody.Builder().add("message",
                        legacyUtils.convertToLegacyNorloges(message,
                                DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy(),
                                room))
                        .add("tribune", room);
                Builder request = new Request.Builder().url(plopToUrl).header("User-Agent", nickname).post(body.build());
                httpClient.newCall(request.build()).enqueue(bouchotPostCallback);
            }
            return true;
        } else {
            return false;
        }
    }

    private class BouchotPostCallBack implements Callback {

        @Override
        public void onResponse(Call call, Response response) throws IOException {
        }

        @Override
        public void onFailure(Call call, IOException e) {
            LogFactory.getLog(this.getClass()).error("post error", e);
        }
    }
}
