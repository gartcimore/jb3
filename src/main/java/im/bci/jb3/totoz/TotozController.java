package im.bci.jb3.totoz;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/totoz")
public class TotozController {

	private File totozDir;

	@Value("${jb3.totoz.dir:}")
	public void setPasteDir(String totozDir) {
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

	@RequestMapping("/img/{totoz}")
	@ResponseBody
	public ResponseEntity<FileSystemResource> img(@PathVariable("totoz") String totoz)
			throws MalformedURLException, IOException {
		File totozFile = new File(totozDir, totoz);
		if (!totozFile.exists()) {
			FileUtils.copyURLToFile(new URL("https://nsfw.totoz.eu/img/" + totoz), totozFile);
		}
		return ResponseEntity.ok().lastModified(totozFile.lastModified()).contentType(detectContentType(totozFile))
				.contentLength(totozFile.length()).body(new FileSystemResource(totozFile));
	}

	private MediaType detectContentType(File totozFile) {
		try {
			return MediaType.parseMediaType(Files.probeContentType(totozFile.toPath()));
		} catch (Exception e) {
			return MediaType.ALL;
		}

	}

}
