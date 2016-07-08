package im.bci.jb3.websocket;

public class WebDirectCoinRQ {
	
	private GetRQ get;
	private PostRQ post;
	private Presence presence;

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

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}

}
