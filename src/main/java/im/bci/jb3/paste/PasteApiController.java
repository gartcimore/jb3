package im.bci.jb3.paste;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import im.bci.jb3.bouchot.data.PasteRepository;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author devnewton
 */
@RestController
@RequestMapping("/api/paste")
public class PasteApiController {

    @Autowired
    private PasteRepository pasteRepository;

    @RequestMapping(path = "/text", method = RequestMethod.POST)
    public PastedMV paste(String ptext) throws FileNotFoundException, IOException {
        PastedMV mv = new PastedMV();
        if (StringUtils.isNotBlank(ptext)) {
            mv.setUrl(pasteRepository.saveTextPaste(ptext));
        }
        return mv;
    }

    @RequestMapping(path = "/image", method = RequestMethod.POST)
    public PastedMV pasteImage(MultipartRequest request) throws FileNotFoundException, IOException {
        PastedMV mv = new PastedMV();
        List<MultipartFile> pimage = request.getFiles("pimage");
        if (null != pimage && pimage.size() > 0) {
            for (int i = pimage.size() - 1; i >= 0; --i) {
                if (!pimage.get(i).isEmpty()) {
                    mv.setUrl(pasteRepository.saveFilePaste(pimage.get(i)));
                    break;
                }
            }
        }
        return mv;
    }

    @RequestMapping(path = "/file", method = RequestMethod.POST)
    public PastedMV pasteFile(MultipartFile pfile) throws FileNotFoundException, IOException {
        PastedMV mv = new PastedMV();
        if (null != pfile && !pfile.isEmpty()) {
            mv.setUrl(pasteRepository.saveFilePaste(pfile));
        }
        return mv;
    }

    @RequestMapping(path = "/totoz/search", method = RequestMethod.GET)
    public ArrayList<Totoz> searchTotoz(@RequestParam(name = "terms", required = true) String terms, @RequestParam(name = "offset", required = false, defaultValue = "0") int offset) throws IOException {
        Document doc = Jsoup.connect("https://nsfw.totoz.eu/search.xml").data("terms", terms)
                .data("offset", Integer.toString(offset)).get();
        ArrayList<Totoz> totozList = new ArrayList<>();
        for (Element t : doc.select("totoz")) {
            Totoz totoz = new Totoz();
            totoz.setName(t.select("name").text());
            totozList.add(totoz);
        }
        return totozList;
    }

    @RequestMapping(path = "/emoji/search", method = RequestMethod.GET)
    public ArrayList<EmojiMV> searchEmoji(@RequestParam(name = "terms", required = true) String terms) throws IOException {
        ArrayList<EmojiMV> emojiList = new ArrayList<>();
        String[] termsTokens = terms.split("\\s+");
        for (Emoji emoji : EmojiManager.getAll()) {
            if (matchesTerms(emoji, termsTokens)) {
                if (!emoji.getAliases().isEmpty()) {
                    EmojiMV mv = new EmojiMV();
                    mv.setCharacter(emoji.getUnicode());
                    mv.setName(emoji.getAliases().get(0));
                    emojiList.add(mv);
                }
            }
        }
        return emojiList;
    }

    private boolean matchesTerms(Emoji emoji, String[] termsToken) {
        for (String token : termsToken) {
            if (emoji.getDescription().contains(token)) {
                return true;
            }
            for (String alias : emoji.getAliases()) {
                if (alias.contains(token)) {
                    return true;
                }
            }
            for (String tag : emoji.getTags()) {
                if (tag.contains(token)) {
                    return true;
                }
            }
        }
        return false;
    }

}
