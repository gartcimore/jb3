package im.bci.jb3.coincoin;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

    private String date = ISODateTimeFormat.date().print(DateTime.now());
    private String nicknameFilter;
    private String messageFilter;
    private String roomFilter;
    private int page = 0;
    private int pageSize = 3600 * 24;//TODO antiflood system to limit number of post per day/hour/min

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public Interval getDateInterval() {
    	try {
    		return ISODateTimeFormat.date().parseDateTime(date).toLocalDate().toInterval();
    	} catch(Exception e) {
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
