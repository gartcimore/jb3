package im.bci.jb3.gateway;

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

}
