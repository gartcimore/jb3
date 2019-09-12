package im.bci.jb3.totoz;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton
 */
@Component
public class TotozCache {

	private File totozDir;
	private final Log LOGGER = LogFactory.getLog(this.getClass());

	@Value("${jb3.totoz.url}")
	private String totozUrl;

	@Value("${jb3.totoz.dir:}")
	public void setTotozDir(String totozDir) {
		if (StringUtils.isEmpty(totozDir)) {
			String cacheDir = System.getenv("XDG_CACHE_HOME");
			if (StringUtils.isEmpty(cacheDir)) {
				cacheDir = new File(System.getProperty("user.home"), ".cache").getAbsolutePath();
			}
			this.totozDir = new File(new File(cacheDir, "jb3"), "totoz");
			this.totozDir.mkdirs();
		} else {
			this.totozDir = new File(totozDir);
		}
	}
	

	public File cacheTotoz(String totoz) throws IOException {
		String totozFilename = URLEncoder.encode(totoz, StandardCharsets.US_ASCII);
		File totozFile = new File(totozDir, totozFilename);
		if (!totozFile.exists()) {
			URL totozImgUrl = UriComponentsBuilder.fromHttpUrl(totozUrl).path("/img/").path(totoz).build().toUri()
					.toURL();
			FileUtils.copyURLToFile(totozImgUrl, totozFile);
		}
		return totozFile;
	}
	
	public void cacheMetadata(String totoz) {
		try {
			String totozPageUrl = UriComponentsBuilder.fromHttpUrl(totozUrl).path("/totoz/").path(totoz).build().toString();
			Document doc = Jsoup.connect(totozPageUrl).get();
			Properties metadata = new Properties();
			metadata.setProperty("author", doc.selectFirst(".username").text());
			metadata.setProperty("tags", doc.select(".tags a").text());
			if(null != doc.selectFirst("span:contains(NSFW)")) {
				metadata.setProperty("nsfw", "true");
			}
			String totozFilename = URLEncoder.encode(totoz, StandardCharsets.US_ASCII) + ".properties";
			File totozFile = new File(totozDir, totozFilename);
			try(FileWriter writer = new FileWriter(totozFile)) {
				metadata.store(writer , null);
			}
		} catch (IOException e) {
			LOGGER.warn("Cannot retrieve totoz metadata for " + totoz, e);
		}
	}

}
