package im.bci.jb3.frontend;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    private String content;
    private int page = 0;
    private static final int pageSize = 50;

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
