package im.bci.jb3.bot;

import im.bci.jb3.data.Post;
import im.bci.jb3.logic.Tribune;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Deeplop implements Bot {

    @Autowired
    private Tribune tribune;

    private static final String NAME = "deeplop";

    @Override
    public void handle(Post post) {
        try {
            if (BotUtils.isBotCall(post, NAME)) {
                URL obj = new URL("http://deeplop.ssz.fr");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeUTF(BotUtils.messageWithoutBotCall(post, NAME));
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                if (200 == responseCode) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    tribune.botPost(NAME, response.toString());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Deeplop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
