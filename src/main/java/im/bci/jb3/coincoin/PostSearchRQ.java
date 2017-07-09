package im.bci.jb3.coincoin;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PostSearchRQ {

	private String since;
	private String until;
	private String nicknameFilter;
	private String messageFilter;
	private String roomFilter;
	private int page = 0;
	private int pageSize = 3600 * 24;// TODO antiflood system to limit number of
										// post per day/hour/min

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

	public DateTime getSinceDate() {
		try {
			return ISODateTimeFormat.date().parseDateTime(since).withTimeAtStartOfDay();
		} catch (Exception e) {
			return null;
		}
	}

	public DateTime getUntilDate() {
		try {
			return ISODateTimeFormat.date().parseDateTime(until).millisOfDay().withMaximumValue();
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
