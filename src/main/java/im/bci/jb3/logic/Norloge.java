package im.bci.jb3.logic;

import im.bci.jb3.data.Post;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Norloge {

    private DateTime time;
    private String bouchot;
    private boolean hasYear, hasMonth, hasDay, hasSeconds;

    public Norloge() {
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

    public Norloge withTime(Date t) {
        setTime(new DateTime(t));
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

    public boolean getHasYear() {
        return hasYear;
    }

    public void setHasYear(boolean hasYear) {
        this.hasYear = hasYear;
    }

    public Norloge withHasYear(boolean hasYear) {
        setHasYear(hasYear);
        return this;
    }

    public boolean getHasMonth() {
        return hasMonth;
    }

    public void setHasMonth(boolean hasMonth) {
        this.hasMonth = hasMonth;
    }

    public Norloge withHasMonth(boolean hasMonth) {
        setHasMonth(hasMonth);
        return this;
    }

    public boolean getHasDay() {
        return hasDay;
    }

    public void setHasDay(boolean hasDay) {
        this.hasDay = hasDay;
    }

    public Norloge withHasDay(boolean hasDay) {
        setHasDay(hasDay);
        return this;
    }

    public boolean getHasSeconds() {
        return hasSeconds;
    }

    public void setHasSeconds(boolean hasSeconds) {
        this.hasSeconds = hasSeconds;
    }

    public Norloge withHasSeconds(boolean hasSeconds) {
        setHasSeconds(hasSeconds);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return !((this.bouchot == null) ? (other.bouchot != null) : !this.bouchot.equals(other.bouchot));
    }

    private static final DateTimeFormatter norlogePrintFormatter = DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").withZoneUTC();

    public int getPrecisionInSeconds() {
        return hasSeconds ? 1 : 60;
    }

    public static String format(Post post) {
        return "<c>" + post.getId() + "</c>";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (null != time) {
            sb.append(norlogePrintFormatter.print(time));
        }
        if (StringUtils.isNotBlank(bouchot)) {
            sb.append('@').append(bouchot);
        }
        return sb.toString();
    }
}
