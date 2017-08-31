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

import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public abstract class AbstractLegacyBouchotGateway implements Gateway {

	@Autowired
	private OkHttpClient httpClient;

	@Autowired
	protected LegacyUtils legacyUtils;

	@Value("${jb3.plopto.url:}")
	private String gatewayUrl;

	private final String room;

	private BouchotPostCallBack bouchotPostCallback;

	protected AbstractLegacyBouchotGateway(String room) {
		this.room = room;
	}

	@PostConstruct
	public void setup() {
		bouchotPostCallback = new BouchotPostCallBack();
	}

	@Override
	public void post(String nickname, String message, String auth) {
		okhttp3.HttpUrl.Builder url = HttpUrl.parse(gatewayUrl).newBuilder();
		okhttp3.FormBody.Builder body = new FormBody.Builder().add("message",
				legacyUtils.convertToLegacyNorloges(message,
						DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy(),
						getRoom()))
				.add("tribune", room);
		Builder request = new Request.Builder().url(url.build()).header("User-Agent", nickname).post(body.build());
		httpClient.newCall(request.build()).enqueue(bouchotPostCallback);
	}

	@Override
	public String getRoom() {
		return room;
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
