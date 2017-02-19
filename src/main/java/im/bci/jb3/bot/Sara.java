package im.bci.jb3.bot;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Sara implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "sara";
    
    private static Pattern PHI = Pattern.compile("\\b(jlm|jean\\sluc|((m[ée]l([ea]n|u))|holo)ch?(on|e)|avenir\\sen\\scommun|phi|france\\sinsoumise?|φ)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public void handle(Post post, UriComponentsBuilder uriBuilder) {
        try {
            if (tribune.isBotCall(post, NAME)) {
                if (PHI.matcher(post.getMessage()).find()) {
                    postAvenirEnCommun(post);
                } else {
                    postJourneeMondiale(post);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Sara.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void postJourneeMondiale(Post post) throws IOException {
        Document doc = Jsoup.connect("http://www.journee-mondiale.com/").get();
        String journee = doc.select("#journeesDuJour > article:nth-child(1) > a:nth-child(1) > h2:nth-child(2)").text();
        if (StringUtils.isNoneBlank(journee)) {
            String message = "Aujourd'hui, c'est la " + journee + ". Hihi!";
            tribune.post(NAME, message, post.getRoom());
        }
    }

    private void postAvenirEnCommun(Post post) throws IOException {
        Document doc = Jsoup.connect("https://laec.fr/hasard").get();
        String sujet = doc.select("#contenu .subject-foreword").text();
        if (StringUtils.isNoneBlank(sujet)) {
            tribune.post(NAME, sujet, post.getRoom());
        }
    }
}
