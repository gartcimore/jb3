/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.jb3.logic;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

public class NorlogeTest {

    @Test
    public void testParseNorloges() {
        System.out.println("parseNorloges");
        String message = "moules< 16:25:11 les norloges du style 12/31#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc #22:30  #tamaman #1234 #n3 #lol@dlfp  #troll@euro?";
        List<Norloge> expResult = new ArrayList<Norloge>();
        expResult.add(new Norloge().withTime(new DateTime().withHourOfDay(16).withMinuteOfHour(25).withSecondOfMinute(11).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(new DateTime().withMonthOfYear(12).withDayOfMonth(31).withHourOfDay(18).withMinuteOfHour(28).withSecondOfMinute(15).withMillisOfSecond(0)));
        expResult.add(new Norloge().withTime(new DateTime().withYear(2012).withMonthOfYear(1).withDayOfMonth(2).withHourOfDay(18).withMinuteOfHour(12).withSecondOfMinute(12).withMillisOfSecond(0)));
        expResult.add(new Norloge().withId("22"));
        expResult.add(new Norloge().withId("tamaman"));
        expResult.add(new Norloge().withId("1234"));
        expResult.add(new Norloge().withId("n3"));
        expResult.add(new Norloge().withId("lol").withBouchot("dlfp"));
        expResult.add(new Norloge().withId("troll").withBouchot("euro"));
        List<Norloge> result = Norloge.parseNorloges(message);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

}
