package im.bci.jb3.bot;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Meteo implements Bot {

	@Autowired
	private Tribune tribune;

	private static final String NAME = "meteo";
	
    private static final Pattern meteoPattern = Pattern.compile("^\\/meteo\\s?(?<lieu>.*)$");

	@Override
	public void handle(Post post, UriComponentsBuilder uriBuilder) {
		try {
			Matcher matcher = meteoPattern.matcher(post.getMessage());
            if (matcher.matches()) {
				postMeteo(matcher.group("lieu"), post);
			}
		} catch (Exception ex) {
			Logger.getLogger(Meteo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void postMeteo(String lieu, Post post) throws IOException {
		if(StringUtils.isBlank(lieu)) {
			lieu = "Nice";
		}
		Document doc = Jsoup.connect("http://www.meteofrance.com/recherche/resultats").data("facet", "previsions")
				.data("query", lieu).post();
		String meteo = doc.select(".day-data").first().text();
		if (StringUtils.isNoneBlank(meteo)) {
			tribune.post(NAME, meteo, post.getRoom());
		}
	}
}
