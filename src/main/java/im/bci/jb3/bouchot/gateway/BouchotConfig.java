package im.bci.jb3.bouchot.gateway;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class BouchotConfig {

    private String room;
    private String getUrl;
    private String postUrl;
    private String lastIdParameterName;
    private String messageContentParameterName;
    private boolean tagsEncoded = true;
    private String referrer;
    private String cookieName;
    private boolean usingCrapCertificate = false;
    private boolean usingXPost = false;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getGetUrl() {
        return getUrl;
    }

    public void setGetUrl(String getUrl) {
        this.getUrl = getUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getLastIdParameterName() {
        return lastIdParameterName;
    }

    public void setLastIdParameterName(String lastIdParameterName) {
        this.lastIdParameterName = lastIdParameterName;
    }

    public String getMessageContentParameterName() {
        return messageContentParameterName;
    }

    public void setMessageContentParameterName(String messageContentParameterName) {
        this.messageContentParameterName = messageContentParameterName;
    }

    public boolean isTagsEncoded() {
        return tagsEncoded;
    }

    public void setTagsEncoded(boolean tagsEncoded) {
        this.tagsEncoded = tagsEncoded;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getReferrer() {
        return referrer;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public boolean isUsingCrapCertificate() {
        return usingCrapCertificate;
    }

    public void setUsingCrapCertificate(boolean crapCertificate) {
        this.usingCrapCertificate = crapCertificate;
    }

	public boolean isUsingXPost() {
		return usingXPost;
	}

	public void setUsingXPost(boolean usingXPost) {
		this.usingXPost = usingXPost;
	}

}
