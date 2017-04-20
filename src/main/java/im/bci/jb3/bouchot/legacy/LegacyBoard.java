package im.bci.jb3.bouchot.legacy;

import java.util.List;

public class LegacyBoard {

    private String timezone;
    private List<LegacyPost> posts;
    private String site;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<LegacyPost> getPosts() {
        return posts;
    }

    public void setPosts(List<LegacyPost> posts) {
        this.posts = posts;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

}
