package cn.fszt.trafficapp.domain;

public class ChatData {

	private int _id;
	private String hdmenuid = "";
	private String nickname = "";
	private String headimg = "";
	private String isdj = "";
	private String content = "";
	private String hdcommentid = "";
	private String createdate = "";
	private String istop = "";
	private String imagepath = "";
	private String voicepath = "";
	private String islive = "";
	
	public String getIsdj() {
		return isdj;
	}

	public void setIsdj(String isdj) {
		this.isdj = isdj;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getHdmenuid() {
		return hdmenuid;
	}

	public void setHdmenuid(String hdmenuid) {
		this.hdmenuid = hdmenuid;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public String getVoicepath() {
		return voicepath;
	}

	public void setVoicepath(String voicepath) {
		this.voicepath = voicepath;
	}

	public String getHdcommentid() {
		return hdcommentid;
	}

	public void setHdcommentid(String hdcommentid) {
		this.hdcommentid = hdcommentid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getIstop() {
		return istop;
	}

	public void setIstop(String istop) {
		this.istop = istop;
	}

	public String getIslive() {
		return islive;
	}

	public void setIslive(String islive) {
		this.islive = islive;
	}

}
