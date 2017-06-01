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
import org.springframework.context.annotation.Scope;

@Component
@Scope("thread")
public class FromLegacyPEGNorlogeConverter {

    @Autowired
    private PostRepository postRepository;

    private Invocable invocable;
    private Object convertLegacyNorloge;

    @PostConstruct
    public void setup() {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        InputStreamReader postToHtmlSource = new InputStreamReader(getClass().getResourceAsStream("/peg/from-legacy-norloge.js"));
        try {
            engine.eval(postToHtmlSource);
            convertLegacyNorloge = engine.eval("jb3_from_legacy_norloge");
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public class NorlogeConverter {

        private String room;

        private DateTime postTime;

        public void setRoom(String room) {
            this.room = room;
        }

        public void setPostTime(DateTime postTime) {
            this.postTime = postTime;
        }

        public String convertFullNorloge(int y, int m, int d, int h, int mi, int s, int indice, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setDateTime(y, m, d, h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(true).withHasMonth(true).withHasDay(true).withHasSeconds(true).withBouchot(bouchot);
            Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, norloge.getTime(), norloge.getTime().plusSeconds(norloge.getPrecisionInSeconds()), indice);
            if (null != post) {
                return Norloge.format(post);
            } else {
                return null;
            }
        }

        private static final int MAX_YEAR_BEFORE = 2;

        public String convertLongNorloge(int m, int d, int h, int mi, int s, int indice, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setYear(postTime.getYear());
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setMonthOfYear(m);
            norlogeTime.setDayOfMonth(d);
            norlogeTime.setTime(h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(true).withHasDay(true).withHasSeconds(true).withBouchot(bouchot);
            for (int year = 0; year <= MAX_YEAR_BEFORE; ++year) {
                DateTime tryTime = norloge.getTime().minusYears(year);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()), indice);
                if (null != post) {
                    return Norloge.format(post);
                }
            }
            return null;
        }

        private static final int MAX_DAY_BEFORE = 100;

        public String convertNormalNorloge(int h, int mi, int s, int indice, String bouchot) {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setZone(LegacyUtils.legacyTimeZone);
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            norlogeTime.setDate(postTime);
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setTime(h, mi, s, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(false).withHasDay(false).withHasSeconds(true).withBouchot(bouchot);
            for (int day = 0; day <= MAX_DAY_BEFORE; ++day) {
                DateTime tryTime = norloge.getTime().minusDays(day);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()), indice);
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
            norlogeTime.setDate(postTime);
            norlogeTime.setSecondOfMinute(0);
            norlogeTime.setTime(h, mi, 0, 0);
            Norloge norloge = new Norloge().withTime(norlogeTime.toDateTime()).withHasYear(false).withHasMonth(false).withHasDay(false).withHasSeconds(false).withBouchot(bouchot);
            for (int day = 0; day <= MAX_DAY_BEFORE; ++day) {
                DateTime tryTime = norloge.getTime().minusDays(day);
                Post post = postRepository.findOne(null != norloge.getBouchot() ? norloge.getBouchot() : room, tryTime, tryTime.plusSeconds(norloge.getPrecisionInSeconds()), 1);
                if (null != post) {
                    return Norloge.format(post);
                }
            }
            return null;
        }

        public String convertIdNorloge(String id, String bouchot) {
            Post post = postRepository.findOne(id);
            if (null == post) {
                GatewayPostId gpid = new GatewayPostId();
                gpid.setPostId(id);
                gpid.setGateway(null != bouchot ? bouchot : room);
                post = postRepository.findOneByGatewayId(gpid);
            }
            if (null != post) {
                return Norloge.format(post);
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

    public String convertFromLegacyNorloge(String message, DateTime postTime, String room) {
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
