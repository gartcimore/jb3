package im.bci.jb3.totoz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
        File totozFile = new File(totozDir, totoz);
        if (!totozFile.exists()) {
            URL totozImgUrl = UriComponentsBuilder.fromHttpUrl(totozUrl).path("/img/").path(totoz).build().toUri().toURL();
            FileUtils.copyURLToFile(totozImgUrl, totozFile);
        }
        return totozFile;
    }

}
