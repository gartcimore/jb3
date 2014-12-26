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
    private String content;
    private int page = 0;
    private static final int pageSize = 50;

    public PostSearchRQ() {
        to = DateTime.now();
        from = to.minusWeeks(1);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
