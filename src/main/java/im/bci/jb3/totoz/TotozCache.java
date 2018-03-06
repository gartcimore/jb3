package im.bci.jb3.totoz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton
 */
@Component
public class TotozCache {

    private final Log LOGGER = LogFactory.getLog(this.getClass());
    private File totozDir;

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

    @Async("mouleExecutor")
    public void saveTotozMetaData(String totoz) {
        try {
            File totozMetadataFile = new File(totozDir, totoz + ".json");
            if (!totozMetadataFile.exists()) {
                URL totozMetadataUrl = UriComponentsBuilder.fromHttpUrl("https://nsfw.totoz.eu/totoz/").path(totoz).path("/info.json").build().toUri().toURL();
                FileUtils.copyURLToFile(totozMetadataUrl, totozMetadataFile);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot save totoz metadata for " + totoz, e);
        }
    }

    public File cacheTotoz(String totoz) throws IOException {
        File totozFile = new File(totozDir, totoz);
        if (!totozFile.exists()) {
            URL totozUrl = UriComponentsBuilder.fromHttpUrl("https://nsfw.totoz.eu/img/").path(totoz).build().toUri().toURL();
            FileUtils.copyURLToFile(totozUrl, totozFile);
        }
        return totozFile;
    }

}
