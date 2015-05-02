/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.jb3.logic;

import im.bci.jb3.legacy.LegacyParser;
import im.bci.jb3.legacy.LegacyProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

public class LegacyParserTest {

    @Test
    public void testParse() {
        System.out.println("parseNorloges");
        String message = " moules&lt; [:totoz] 16:25:11 les norloges du style 12/31#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc et #tamaman #1234 #n3 #lol@dlfp  #troll@euro.net? et 12:30?";
        final List<Object> expResult = new ArrayList<Object>();
        expResult.add("moules");
        expResult.add("totoz");
        expResult.add(new Norloge().withTime(new DateTime().withZone(DateTimeZone.UTC).withHourOfDay(16).withMinuteOfHour(25).withSecondOfMinute(11).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(new DateTime().withZone(DateTimeZone.UTC).withMonthOfYear(12).withDayOfMonth(31).withHourOfDay(18).withMinuteOfHour(28).withSecondOfMinute(15).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(new DateTime().withZone(DateTimeZone.UTC).withYear(2012).withMonthOfYear(1).withDayOfMonth(2).withHourOfDay(18).withMinuteOfHour(12).withSecondOfMinute(12).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(new DateTime().withZone(DateTimeZone.UTC).withHourOfDay(12).withMinuteOfHour(30).withSecondOfMinute(0).withMillisOfSecond(0)));
        LegacyParser.forEach(message, new LegacyProcessor() {

            private int i;

            @Override
            public void processNorloge(Norloge norloge, Matcher matcher) {
                assertEquals(expResult.get(i++), norloge);
            }

            @Override
            public void processTotoz(String totoz, Matcher matcher) {
                assertEquals(expResult.get(i++), totoz);

            }

            @Override
            public void processBigorno(String bigorno, Matcher matcher) {
                assertEquals(expResult.get(i++), bigorno);
            }

            @Override
            public void end(Matcher matcher) {
            }
        });
    }

}
