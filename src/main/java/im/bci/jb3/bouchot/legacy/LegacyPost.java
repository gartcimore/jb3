package im.bci.jb3.bouchot.legacy;

public class LegacyPost {

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTribune() {
		return tribune;
	}

	public void setTribune(String tribune) {
		this.tribune = tribune;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	private String tribune;
    private String info;
    private String login;
    private String message;
    private String time;
    private long id;

}
