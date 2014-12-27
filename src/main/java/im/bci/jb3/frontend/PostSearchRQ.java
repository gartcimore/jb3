package im.bci.jb3.frontend;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    @DateTimeFormat(pattern = "yyyy/MM/dd#HH:mm:ss")
    private DateTime from;
    @DateTimeFormat(pattern = "yyyy/MM/dd#HH:mm:ss")
    private DateTime to;
    private String nicknameFilter;
    private String messageFilter;
    private int page = 0;
    private static final int pageSize = 50;

    public PostSearchRQ() {
        DateTime now = DateTime.now();
        to = now.plusDays(1);
        from = now.minusWeeks(1);
    }

    public DateTime getFrom() {
        return from;
    }

    public void setFrom(DateTime from) {
        this.from = from;
    }

    public DateTime getTo() {
        return to;
    }

    public void setTo(DateTime to) {
        this.to = to;
    }

    public int getPageSize() {
        return pageSize;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
