package cn.fszt.trafficapp.domain;

public class PinglunNewsData {

	private String nickname = "";
	private String headimg = "";
	private String content = "";
	private String hdnewcommentid = "";
	private String createdate = "";
	private String imagepath = "";
	private String voicepath = "";
	private String videopath = "";
	private String istop = "";
	

	public PinglunNewsData() {
	}


	public PinglunNewsData(String nickname, String headimg, String content,String hdnewcommentid,
			String createdate, String imagepath, String voicepath,
			String videopath, String istop) {
		super();
		this.nickname = nickname;
		this.headimg = headimg;
		this.content = content;
		this.hdnewcommentid = hdnewcommentid;
		this.createdate = createdate;
		this.imagepath = imagepath;
		this.voicepath = voicepath;
		this.videopath = videopath;
		this.istop = istop;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getHdnewcommentid() {
		return hdnewcommentid;
	}


	public void setHdnewcommentid(String hdnewcommentid) {
		this.hdnewcommentid = hdnewcommentid;
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


	public String getVideopath() {
		return videopath;
	}


	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}


	public String getIstop() {
		return istop;
	}


	public void setIstop(String istop) {
		this.istop = istop;
	}

	

	


}
