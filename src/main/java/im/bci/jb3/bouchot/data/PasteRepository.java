package im.bci.jb3.bouchot.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class PasteRepository {

    private File pasteDir;

    @Value("${jb3.paste.dir:}")
    public void setPasteDir(String pasteDir) {
        if (StringUtils.isEmpty(pasteDir)) {
            String cacheDir = System.getenv("XDG_CACHE_HOME");
            if (StringUtils.isEmpty(cacheDir)) {
                cacheDir = new File(System.getProperty("user.home"), ".cache").getAbsolutePath();
            }
            this.pasteDir = new File(new File(cacheDir, "jb3"), "pasted");
            this.pasteDir.mkdirs();
        } else {
            this.pasteDir = new File(pasteDir);
        }
    }

    public File getPasteDir() {
        return pasteDir;
    }

    @Value("${jb3.paste.maxFiles:3}")
    private int pasteMaxFiles;

    public String saveFilePaste(MultipartFile multipartFile) throws FileNotFoundException, IOException {
        String filename = UUID.randomUUID() + ".zip";
        try (FileOutputStream fos = new FileOutputStream(new File(pasteDir, filename));
                ZipOutputStream zos = new ZipOutputStream(fos);
                InputStream is = multipartFile.getInputStream()) {
            FileCopyUtils.copy(is, zos);
        }
        cleanup();
        return buildPasteUrl(filename);
    }

    private String buildPasteUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/pasted/").path(filename).build()
                .toString();
    }

    private void cleanup() {
        File[] files = pasteDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isFile();
            }
        });
        if (files.length > pasteMaxFiles) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });
            for (int i = pasteMaxFiles; i < files.length; ++i) {
                files[i].delete();
            }
        }
    }

    public String saveTextPaste(String ptext) throws FileNotFoundException, IOException {
        String filename = UUID.randomUUID() + ".txt";
        PrintWriter w = new PrintWriter(new File(pasteDir, filename));
        try {
            w.print(ptext);
        } finally {
            w.close();
        }
        cleanup();
        return buildPasteUrl(filename);
    }
}
