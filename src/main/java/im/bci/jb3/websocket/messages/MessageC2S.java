package im.bci.jb3.websocket.messages;

import im.bci.jb3.websocket.messages.c2s.GetC2S;
import im.bci.jb3.websocket.messages.c2s.GetNorlogeC2S;
import im.bci.jb3.websocket.messages.c2s.PostC2S;
import im.bci.jb3.websocket.messages.data.Presence;

public class MessageC2S {
	
	private GetC2S get;
	private PostC2S post;
	private Presence presence;
	private GetNorlogeC2S getNorloge;

	public GetC2S getGet() {
		return get;
	}

	public void setGet(GetC2S get) {
		this.get = get;
	}

	public PostC2S getPost() {
		return post;
	}

	public void setPost(PostC2S post) {
		this.post = post;
	}

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}

    public GetNorlogeC2S getGetNorloge() {
        return getNorloge;
    }

    public void setGetNorloge(GetNorlogeC2S getNorloge) {
        this.getNorloge = getNorloge;
    }

}
