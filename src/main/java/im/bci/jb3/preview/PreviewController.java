package im.bci.jb3.preview;

import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/preview")
public class PreviewController {

    private final Log LOGGER = LogFactory.getLog(this.getClass());

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String url(@RequestParam(name = "url", required = true) String url, Model model) {
        model.addAttribute("preview", previewUrl(UriComponentsBuilder.fromHttpUrl(url).build()));
        return "preview";
    }

    private String previewUrl(UriComponents url) {
        try {
            switch (url.getHost()) {
                case "twitter.com":
                case "mobile.twitter.com":
                    return previewTwittos(url);
                default:
                    return "";
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot preview " + url, e);
            return "";
        }
    }

    public static class Twittos {

        String html;

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

    }

    private String previewTwittos(UriComponents twittosUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        Twittos twittos = restTemplate.exchange("https://publish.twitter.com/oembed?hide_media=true&hide_thread=true&omit_script=true&dnt=true&url={url}", HttpMethod.GET, entity, Twittos.class, Collections.singletonMap("url", twittosUrl.toString())).getBody();
        String text = Jsoup.parse(twittos.getHtml()).text();
        return Jsoup.clean(text, Whitelist.none());
    }

}
