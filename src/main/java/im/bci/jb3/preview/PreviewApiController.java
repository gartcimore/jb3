package im.bci.jb3.preview;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preview")
public class PreviewApiController {

	private final Log LOGGER = LogFactory.getLog(this.getClass());

	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public Preview url(@RequestParam(name = "url", required = true) String url, Model model) {
		Preview preview = new Preview();
		try {
			Document doc = Jsoup.connect(url).get();
			extractTitle(preview, doc);
			extractImage(preview, doc);
		} catch (IOException e) {
			LOGGER.warn("Cannot preview url " + url, e);
		}
		return preview;
	}

	private void extractImage(Preview preview, Document doc) {
		Elements metaOgImage = doc.select("meta[property=og:image]");
		if (metaOgImage != null) {
			String image = metaOgImage.attr("content");
			image = Jsoup.clean(image, Whitelist.none());
			if (StringUtils.isNotBlank(image)) {
				preview.setImage(image);
			}
		}
	}

	private void extractTitle(Preview preview, Document doc) {
		Elements metaOgTitle = doc.select("meta[property=og:title]");
		String title = null != metaOgTitle ? metaOgTitle.attr("content") : doc.title();
		if (StringUtils.isNotBlank(title)) {
			preview.setTitle(Jsoup.clean(title, Whitelist.none()));
		}
	}

}
