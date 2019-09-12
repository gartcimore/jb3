package im.bci.jb3.totoz;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	private TotozCache cache;

	@RequestMapping("/img/{totoz}")
	@ResponseBody
	public ResponseEntity<FileSystemResource> img(@PathVariable("totoz") String totoz)
			throws MalformedURLException, IOException {
		File totozFile = cache.cacheTotoz(totoz);
		cache.cacheMetadata(totoz);
		return ResponseEntity.ok().lastModified(totozFile.lastModified()).contentType(detectContentType(totozFile))
				.contentLength(totozFile.length()).body(new FileSystemResource(totozFile));
	}

	private MediaType detectContentType(File totozFile) {
		try {
			return MediaType.parseMediaType(Files.probeContentType(totozFile.toPath()));
		} catch (Exception e) {
			return MediaType.IMAGE_PNG;
		}

	}

}
