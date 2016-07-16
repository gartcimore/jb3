package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Tribune;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Blague implements Bot {

	@Autowired
	private Tribune tribune;

	private static final String NAME = "blague";
	private static final int MAX_BLAGUE_LINES = 42;

	@Override
	public void handle(Post post, UriComponentsBuilder uriBuilder) {
		try {
			if (tribune.isBotCall(post, NAME)) {
				Document doc = Jsoup.connect("http://www.blague.org/").get();
				doc.select("#humour h1").remove();
				String blague = Jsoup.clean(doc.select("#humour").first().html(), "", Whitelist.none(),
						new OutputSettings().prettyPrint(false));
				int linePosted = 0;
				for (String line : blague.split("\\n")) {
					if(linePosted++ >= MAX_BLAGUE_LINES) {
						break;
					}
					tribune.post(NAME, line, post.getRoom());
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Blague.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
