package im.bci.jb3.frontend;

import org.joda.time.DateTime;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    private long from;
    private long to;
    private String nicknameFilter;
    private String messageFilter;
    private int page = 0;
    private int pageSize = 50;

    public PostSearchRQ() {
        DateTime now = DateTime.now();
        to = now.plusDays(1).getMillis();
        from = now.minusWeeks(1).getMillis();
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
