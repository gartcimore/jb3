package im.bci.jb3.data;

import org.springframework.util.ObjectUtils;

public class PostRevisor {

    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public PostRevisor withPost(Post post) {
        setPost(post);
        return this;
    }

    public boolean canRevise(Post postToRevise) {
        if (null != post && null != postToRevise && null == postToRevise.getGatewayPostId()) {
            return ObjectUtils.nullSafeEquals(post.getNickname(), postToRevise.getNickname()) && ObjectUtils.nullSafeEquals(post.getRoom(), postToRevise.getRoom());
        }
        return false;
    }

}
