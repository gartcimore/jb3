package im.bci.jb3.websocket;

public class WebDirectCoinRQ {
	
	private GetRQ get;
	private PostRQ post;
	private PresenceRQ presence;

	public GetRQ getGet() {
		return get;
	}

	public void setGet(GetRQ get) {
		this.get = get;
	}

	public PostRQ getPost() {
		return post;
	}

	public void setPost(PostRQ post) {
		this.post = post;
	}

	public PresenceRQ getPresence() {
		return presence;
	}

	public void setPresence(PresenceRQ presence) {
		this.presence = presence;
	}

}
