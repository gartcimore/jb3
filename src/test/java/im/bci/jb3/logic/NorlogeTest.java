package im.bci.jb3.logic;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;

import im.bci.jb3.bouchot.legacy.LegacyUtils;
import im.bci.jb3.bouchot.logic.Norloge;

import static org.junit.Assert.*;

public class NorlogeTest {

    @Test
    public void testParseNorloges() {
        System.out.println("parseNorloges");
        String message = " moules< 16:25:11 les posts du style 12/31#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc et #tamaman #1234 #n3 #lol@dlfp  #troll@euro.net? et 12:30?";
        List<Norloge> expResult = new ArrayList<Norloge>();
        expResult.add(new Norloge().withTime(DateTime.now(LegacyUtils.legacyTimeZone).withHourOfDay(16).withMinuteOfHour(25).withSecondOfMinute(11).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(DateTime.now(LegacyUtils.legacyTimeZone).withMonthOfYear(12).withDayOfMonth(31).withHourOfDay(18).withMinuteOfHour(28).withSecondOfMinute(15).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(DateTime.now(LegacyUtils.legacyTimeZone).withYear(2012).withMonthOfYear(1).withDayOfMonth(2).withHourOfDay(18).withMinuteOfHour(12).withSecondOfMinute(12).withMillisOfSecond(0)));
        expResult.add(new Norloge().withId("tamaman"));
        expResult.add(new Norloge().withId("1234"));
        expResult.add(new Norloge().withId("n3"));
        expResult.add(new Norloge().withId("lol").withBouchot("dlfp"));
        expResult.add(new Norloge().withId("troll").withBouchot("euro.net"));
        expResult.add(new Norloge().withTime(DateTime.now(LegacyUtils.legacyTimeZone).withHourOfDay(12).withMinuteOfHour(30).withSecondOfMinute(0).withMillisOfSecond(0)));
        List<Norloge> result = Norloge.parseNorloges(message);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

}
