package im.bci.jb3.bouchot.legacy;

import im.bci.jb3.bouchot.data.GatewayPostId;
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
import im.bci.jb3.bouchot.logic.Norloge;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Scope;

@Component
public class LegacyNorlogeConverter {

    public static class NorlogeFormatter {

        public String format(Post post) {
            return Norloge.norlogePrintFormatter.print(post.getTime());
        }
    }

    private Invocable invocable;
    private Object convertLegacyNorloge;

    @PostConstruct
    public void setup() {

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        InputStreamReader postToHtmlSource = new InputStreamReader(getClass().getResourceAsStream("/peg/convert-legacy-norloge.js"));
        try {
            engine.eval(postToHtmlSource);
            convertLegacyNorloge = engine.eval("jb3_convert_legacy_norloge");
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Component
    @Scope("prototype")
    public static class NorlogeConverter {

        @Autowired
        private PostRepository postRepository;

        private String room;

        public String convertFullNorloge(int y, int m, int d, int h, int mi, int s, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setDateTime(y, m, d, h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(true).withHasMonth(true).withHasDay(true).withHasSeconds(true).withBouchot(bouchot);
            Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, norloge.getTime(), norloge.getTime().plusSeconds(norloge.getPrecisionInSeconds()));
            if (null != post) {
                return Norloge.format(post);
            } else {
                return null;
            }
        }

        private static final int MAX_YEAR_BEFORE = 2;

        public String convertLongNorloge(int m, int d, int h, int mi, int s, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setMonthOfYear(m);
            norlogeTime.setDayOfMonth(d);
            norlogeTime.setTime(h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(true).withHasDay(true).withHasSeconds(true).withBouchot(bouchot);
            for (int year = 0; year <= MAX_YEAR_BEFORE; ++year) {
                DateTime tryTime = norloge.getTime().minusYears(year);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()));
                if (null != post) {
                    return Norloge.format(post);
                }
            }
            return null;
        }

        private static final int MAX_DAY_BEFORE = 100;

        public String convertNormalNorloge(int h, int mi, int s, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setTime(h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(false).withHasDay(false).withHasSeconds(true).withBouchot(bouchot);
            for (int day = 0; day <= MAX_DAY_BEFORE; ++day) {
                DateTime tryTime = norloge.getTime().minusDays(day);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()));
                if (null != post) {
                    return Norloge.format(post);
                }
            }
            return null;
        }

        public String convertShortNorloge(int h, int mi, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setTime(h, mi, 0, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(false).withHasDay(false).withHasSeconds(false).withBouchot(bouchot);
            for (int day = 0; day <= MAX_DAY_BEFORE; ++day) {
                DateTime tryTime = norloge.getTime().minusDays(day);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()));
                if (null != post) {
                    return Norloge.format(post);
                }
            }
            return null;
        }

        public String convertIdNorloge(String id, String bouchot) {
            Post post = postRepository.findOne(id);
            if(null == post) {
                GatewayPostId gpid = new GatewayPostId();
                gpid.setPostId(id);
                gpid.setGateway(null != bouchot ? bouchot : room);
                post = postRepository.findOneByGatewayId(gpid);
            }
            if(null != post) {
                return Norloge.format(post);
            }
            return null;
        }
    }

    @Autowired
    private FactoryBean<NorlogeConverter> norlogeConverterFactory;

    public static class ParseOptions {

        private NorlogeConverter norlogeConverter;

        public NorlogeConverter getNorlogeConverter() {
            return norlogeConverter;
        }

        public void setNorlogeConverter(NorlogeConverter norlogeConverter) {
            this.norlogeConverter = norlogeConverter;
        }
    }

    public String convertFromLegacyNorloge(String message, String room) throws Exception {
        ParseOptions options = new ParseOptions();
        options.setNorlogeConverter(norlogeConverterFactory.getObject());
        return invocable.invokeMethod(convertLegacyNorloge, "parse", message, options).toString();
    }

}
