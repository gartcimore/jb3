package im.bci.jb3.coincoin;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import im.bci.jb3.bouchot.legacy.LegacyUtils;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(LegacyUtils.legacyTimeZone);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss").withZone(LegacyUtils.legacyTimeZone);

    private String since, sinceTime;
    private String until, untilTime;
    private String nicknameFilter;
    private String messageFilter;
    private String roomFilter;
    private int page = 0;
    private int pageSize = 3600 * 2;

    public enum Sort {
        TIME_ASC,
        TIME_DESC,
        RELEVANCE
    }

    private Sort sort;

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
    }

    public String getSinceTime() {
        return sinceTime;
    }

    public void setSinceTime(String sinceTime) {
        this.sinceTime = sinceTime;
    }

    public String getUntilTime() {
        return untilTime;
    }

    public void setUntilTime(String untilTime) {
        this.untilTime = untilTime;
    }

    public DateTime getSinceDate() {
        try {
            if (StringUtils.isNotBlank(sinceTime)) {
                return DATETIME_FORMATTER.parseDateTime(since + "T" + sinceTime);
            } else {
                return DATE_FORMATTER.parseDateTime(since).withTimeAtStartOfDay();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public DateTime getUntilDate() {
        try {
            if (StringUtils.isNotBlank(untilTime)) {
                return DATETIME_FORMATTER.parseDateTime(until + "T" + untilTime);
            } else {
                return DATE_FORMATTER.parseDateTime(until).millisOfDay().withMaximumValue();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getNicknameFilter() {
        return nicknameFilter;
    }

    public void setNicknameFilter(String nicknameFilter) {
        this.nicknameFilter = nicknameFilter;
    }

    public String getMessageFilter() {
        return messageFilter;
    }

    public void setMessageFilter(String messageFilter) {
        this.messageFilter = messageFilter;
    }

    public String getRoomFilter() {
        return roomFilter;
    }

    public void setRoomFilter(String roomFilter) {
        this.roomFilter = roomFilter;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
