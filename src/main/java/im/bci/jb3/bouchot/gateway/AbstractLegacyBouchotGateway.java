package im.bci.jb3.bouchot.gateway;

import im.bci.jb3.bouchot.data.GatewayPostId;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import im.bci.jb3.bouchot.legacy.LegacyUtils;
import im.bci.jb3.bouchot.logic.CleanUtils;
import im.bci.jb3.event.NewPostsEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Request.Builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.io.Reader;
import java.io.StringReader;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public abstract class AbstractLegacyBouchotGateway implements Gateway, Trigger, Runnable {

	@Autowired
	private OkHttpClient httpClient;

	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private PostRepository postPepository;
	@Autowired
	protected LegacyUtils legacyUtils;
	private long lastPostId = -1;

	private final BouchotConfig config;
	private final BouchotAdaptiveRefreshComputer adaptativeRefreshComputer = new BouchotAdaptiveRefreshComputer();

	private final ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(
			Executors.newSingleThreadScheduledExecutor());

	protected AbstractLegacyBouchotGateway(BouchotConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void setup() {
		scheduler.schedule(this, this);
	}

	protected void importPosts() {
		okhttp3.HttpUrl.Builder url = HttpUrl.parse(config.getGetUrl()).newBuilder();
		if (null != config.getLastIdParameterName()) {
			url.addQueryParameter(config.getLastIdParameterName(), String.valueOf(lastPostId));
		}
		Request request = new Request.Builder().url(url.build()).header("User-Agent", "jb3").get().build();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				scheduler.schedule(() -> {
                                    parsePosts(response);
                                }, new Date());
			}

			@Override
			public void onFailure(Call call, IOException e) {
				adaptativeRefreshComputer.error();
				LogFactory.getLog(this.getClass()).warn("get http error", e);
			}
		});
	}

	private synchronized void parsePosts(Response response) {
		ArrayList<Post> newPosts = new ArrayList<>();
		try {
			switch (config.getBackendFormat()) {
			case TSV:
				parseTSV(response, newPosts);
				break;
			case XML:
				parseXML(response, newPosts);
				break;
			}
		} catch (NumberFormatException | IOException e) {
			LogFactory.getLog(this.getClass()).error("post error", e);
		}
		adaptativeRefreshComputer.analyseBouchotPostsResponse(newPosts);
		if (!newPosts.isEmpty()) {
			publisher.publishEvent(new NewPostsEvent(newPosts));
		}
	}

	private void parseXML(Response response, ArrayList<Post> newPosts) throws IOException {
		Document doc = Jsoup.parse(response.body().string(), "", Parser.xmlParser());
		Elements postsToImport = doc.select("post");
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
				String nickname = decodeTags(postToImport.select("login").first());
				if (StringUtils.isBlank(nickname)) {
					nickname = CleanUtils.truncateNickname(decodeTags(postToImport.select("info").first()));
				}
				post.setNickname(CleanUtils.cleanNickname(nickname));
				post.setRoom(config.getRoom());
				DateTime postTimeRounded = LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.attr("time"))
						.secondOfMinute().roundFloorCopy();
				long nbPostsAtSameSecond = postPepository.countPosts(postTimeRounded, postTimeRounded.plusSeconds(1),
						config.getRoom());
				post.setTime(postTimeRounded.withMillisOfSecond((int) nbPostsAtSameSecond));
				post.setMessage(legacyUtils.convertFromLegacyNorloges(
						CleanUtils.cleanMessage(
								CleanUtils.truncateMessage(decodeTags(postToImport.select("message").first()))),
						post.getTime(), config.getRoom()));
				postPepository.save(post);
				newPosts.add(post);
			}
			if (postId > lastPostId) {
				lastPostId = postId;
			}
		}

	}

	private void parseTSV(Response response, ArrayList<Post> newPosts) throws NumberFormatException, IOException {
		try (Reader in = new StringReader(response.body().string())) {
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
					DateTime postTimeRounded = LegacyUtils.legacyPostTimeFormatter.parseDateTime(postToImport.get(1))
							.secondOfMinute().roundFloorCopy();
					long nbPostsAtSameSecond = postPepository.countPosts(postTimeRounded,
							postTimeRounded.plusSeconds(1), config.getRoom());
					post.setTime(postTimeRounded.withMillisOfSecond((int) nbPostsAtSameSecond));
					String nickname = decodeTags(postToImport.get(3));
					if (StringUtils.isBlank(nickname)) {
						nickname = CleanUtils.truncateNickname(decodeTags(postToImport.get(2)));
					}
					post.setNickname(CleanUtils.cleanNickname(nickname));
					post.setMessage(legacyUtils.convertFromLegacyNorloges(
							CleanUtils.cleanMessage(CleanUtils.truncateMessage(decodeTags(postToImport.get(4)))),
							post.getTime(), config.getRoom()));
					postPepository.save(post);
					newPosts.add(post);
				}
				if (postId > lastPostId) {
					lastPostId = postId;
				}
			}
		}
	}

	@Override
	public void post(String nickname, String message, String auth) {
		okhttp3.HttpUrl.Builder url = HttpUrl.parse(config.getPostUrl()).newBuilder();
		if (null != config.getLastIdParameterName()) {
			url.addQueryParameter(config.getLastIdParameterName(), String.valueOf(lastPostId));
		}
		okhttp3.FormBody.Builder body = new FormBody.Builder().add(config.getMessageContentParameterName(),
				legacyUtils.convertToLegacyNorloges(message,
						DateTime.now().withZone(LegacyUtils.legacyTimeZone).secondOfMinute().roundFloorCopy(),
						getRoom()));
		if (null != config.getLastIdParameterName()) {
			body.add(config.getLastIdParameterName(), String.valueOf(lastPostId));
			url.addQueryParameter(config.getLastIdParameterName(), String.valueOf(lastPostId));
		}
		Builder request = new Request.Builder().url(url.build()).header("User-Agent", nickname).post(body.build());
		httpClient.newCall(request.build()).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (config.isUsingXPost()) {
					scheduler.schedule(() -> {
                                            parsePosts(response);
                                        }, new Date());
				} else {
					importPosts();
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				LogFactory.getLog(this.getClass()).error("post error", e);
			}
		});
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
