package im.bci.jb3.coincoin;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    private String date;
    private String nicknameFilter;
    private String messageFilter;
    private String roomFilter;
    private int page = 0;
    private int pageSize = 50;
    private Date to;
    private Date from;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        DateTime day = StringUtils.isEmpty(date) ? DateTime.now() : new DateTime(date);
        day = day.withTimeAtStartOfDay();
        from = day.toDate();
        to = day.plusDays(1).toDate();
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
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

    public int getPreviousPage() {
        return page - 1;
    }

    public int getNextPage() {
        return page + 1;
    }

}
