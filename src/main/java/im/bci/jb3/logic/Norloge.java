package im.bci.jb3.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Norloge {

    private String id;
    private DateTime time;
    private String bouchot;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Norloge withId(String i) {
        setId(i);
        return this;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public Norloge withTime(DateTime t) {
        setTime(t);
        return this;
    }

    public String getBouchot() {
        return bouchot;
    }

    public void setBouchot(String bouchot) {
        this.bouchot = bouchot;
    }

    public Norloge withBouchot(String b) {
        setBouchot(b);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 67 * hash + (this.bouchot != null ? this.bouchot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Norloge other = (Norloge) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return !((this.bouchot == null) ? (other.bouchot != null) : !this.bouchot.equals(other.bouchot));
    }

    private static final DateTimeFormatter norlogePrintFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss");
    private static final List<DateTimeFormatter> norlogeParseFormatters = Arrays.asList(DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss"), DateTimeFormat.forPattern("MM/dd#HH:mm:ss"), DateTimeFormat.forPattern("HH:mm:ss"), DateTimeFormat.forPattern("HH:mm:ss"));
    private static final Pattern postIdBasedPattern = Pattern.compile("#(?<id>\\w*)(@(?<bouchot>[\\w.]*))?");
    private static final Pattern timeBasedPattern = Pattern.compile("(?<time>.*)(@(?<bouchot>[\\w.]*))?");

    public static List<Norloge> parseNorloges(String message) {
        List<Norloge> result = new ArrayList<Norloge>();
        Scanner scanner = new Scanner(message);
        while (scanner.hasNext()) {
            String item = scanner.next();
            Norloge norloge;
            if (item.startsWith("#")) {
                norloge = parsePostIdBasedNorloge(item);
            } else {
                norloge = parseTimeBasedNorloge(item);
            }
            if (null != norloge) {
                result.add(norloge);
            }
        }
        return result;
    }

    private static Norloge parsePostIdBasedNorloge(String item) {
        Matcher matcher = postIdBasedPattern.matcher(item);
        if (matcher.find()) {
            Norloge norloge = new Norloge();
            norloge.setId(matcher.group("id"));
            norloge.setBouchot(matcher.group("bouchot"));
            return norloge;
        }
        return null;
    }

    private static Norloge parseTimeBasedNorloge(String item) {
        Matcher matcher = timeBasedPattern.matcher(item);
        if (matcher.find()) {
            DateTime norlogeTime = parseNorlogeTime(matcher.group("time"));
            if (null != norlogeTime) {
                Norloge norloge = new Norloge();
                norloge.setTime(norlogeTime);
                norloge.setBouchot(matcher.group("bouchot"));
                return norloge;
            }
        }
        return null;
    }

    private static DateTime parseNorlogeTime(String item) {
        for (DateTimeFormatter format : norlogeParseFormatters) {
            DateTime norlogeTime = parseNorlogeTimeWithFormat(item, format);
            if (null != norlogeTime) {
                return norlogeTime;
            }
        }
        return null;
    }

    private static DateTime parseNorlogeTimeWithFormat(String item, DateTimeFormatter format) {
        try {
            MutableDateTime norlogeTime = new MutableDateTime();
            norlogeTime.setRounding(norlogeTime.getChronology().secondOfMinute());
            if (format.parseInto(norlogeTime, item, 0) >= 0) {
                return norlogeTime.toDateTime();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(id)) {
            sb.append('#');
            sb.append(id);
        }
        if (null != time) {
            sb.append(norlogePrintFormatter.print(time));
        }
        if (StringUtils.isNotBlank(bouchot)) {
            sb.append('@').append(bouchot);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        final String post = "moules< 16:25:11 les norloges du style 12/02#18:28:15 ou 2012/01/02#18:12:12 c'est conforme à la rfc #22:30  #tamaman #1234 #n3 #lol@dlfp  #troll@euro?";
        for (Norloge norloge : parseNorloges(post)) {
            System.out.println(norloge);
        }
    }

}
