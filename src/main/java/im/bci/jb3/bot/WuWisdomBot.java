package im.bci.jb3.bot;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Norloge;
import im.bci.jb3.bouchot.logic.Tribune;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WuWisdomBot implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "wisdom";

    @Override
    public void handle(Post post, UriComponentsBuilder uriBuilder) {
        try {
            if (tribune.isBotCall(post, NAME)) {
                HttpGet request = null;

                try {

//                    Document doc = Jsoup.connect("https://wutangclan.net/wu-wisdom/").get();
//                    Element wisdom = doc.select("blockquote.wu-wisdom").first();
//                    wisdom.text();

                    String url = "https://wutangclan.net/wu-wisdom/";
                    HttpClient client = HttpClientBuilder.create().build();
                    request = new HttpGet(url);

                    request.addHeader("User-Agent", "Apache HTTPClient");
                    HttpResponse response = client.execute(request);

                    HttpEntity entity = response.getEntity();
                    String content = EntityUtils.toString(entity);
                    final String quote = extractWisdom(content);
                    tribune.post(NAME, String.format("%s Here some Wu-Tang wisdom : %s", Norloge.format(post), quote)  , post.getRoom());

                } finally {

                    if (request != null) {

                        request.releaseConnection();
                    }
                }
            }
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).error(String.format("%s bot error", NAME), ex);
        }
    }

    static String extractWisdom(String content) {
        Document doc = Jsoup.parse(content, "UTF-8");
        Element wisdom = doc.select("blockquote.wu-wisdom").first();
        return wisdom.text();
    }
}
