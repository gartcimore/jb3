package im.bci.jb3.bouchot.legacy;

import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.PostRepository;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Scope;

@Component
@Scope("thread")
public class ToLegacyPEGNorlogeConverter {

    @Autowired
    private PostRepository postRepository;

    private Invocable invocable;
    private Object convertLegacyNorloge;

    @PostConstruct
    public void setup() {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        InputStreamReader postToHtmlSource = new InputStreamReader(getClass().getResourceAsStream("/peg/to-legacy-norloge.js"));
        try {
            engine.eval(postToHtmlSource);
            convertLegacyNorloge = engine.eval("jb3_to_legacy_norloge");
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
    private static final DateTimeFormatter TO_ISO_NORLOGE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    private static final DateTimeFormatter TO_NORMAL_NORLOGE_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    private static final DateTimeComparator DATE_COMPARATOR = DateTimeComparator.getDateOnlyInstance();

    public class NorlogeConverter {

        private String room;

        private DateTime postTime;

        public void setRoom(String room) {
            this.room = room;
        }

        public void setPostTime(DateTime postTime) {
            this.postTime = postTime;
        }

        public String convertIdNorloge(String id) {
            Post post = postRepository.findOne(id);
            if (null != post) {
                String legacyNorloge;
                if (DATE_COMPARATOR.compare(post.getTime(), postTime) == 0) {
                    legacyNorloge = TO_NORMAL_NORLOGE_FORMATTER.print(post.getTime());
                } else {
                    legacyNorloge = TO_ISO_NORLOGE_FORMATTER.print(post.getTime());
                }
                if (!StringUtils.equals(post.getRoom(), room)) {
                    legacyNorloge += "@" + post.getRoom();
                }
                return legacyNorloge;
            }
            return null;
        }
    }

    public static class ParseOptions {

        private NorlogeConverter norlogeConverter;

        public NorlogeConverter getNorlogeConverter() {
            return norlogeConverter;
        }

        public void setNorlogeConverter(NorlogeConverter norlogeConverter) {
            this.norlogeConverter = norlogeConverter;
        }
    }

    public String convertToLegacyNorloges(String message, DateTime postTime, String room) {
        try {
            ParseOptions options = new ParseOptions();
            NorlogeConverter converter = new NorlogeConverter();
            converter.setPostTime(postTime);
            converter.setRoom(room);
            options.setNorlogeConverter(converter);
            return invocable.invokeMethod(convertLegacyNorloge, "parse", message, options).toString();
        } catch (Exception ex) {
            return message;
        }
    }

}
