package im.bci.jb3.bouchot.data;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class GatewayPostId {

    @Indexed
    private String gateway;
    @Indexed
    private String postId;

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

}
