package cn.fszt.trafficapp.domain;

public class ShareDetailReplyData {

	private String connected_uid;
	private String nickname;
	private String headimg;
	private String isdj;
	private String isdjpath;
	private String content;
	private String stcollectreplyid;
	private String imagepath;
	private String voicepath;
	private String createdate;
	
	public String getIsdj() {
		return isdj;
	}
	public void setIsdj(String isdj) {
		this.isdj = isdj;
	}
	public String getIsdjpath() {
		return isdjpath;
	}
	public void setIsdjpath(String isdjpath) {
		this.isdjpath = isdjpath;
	}
	public String getStcollectreplyid() {
		return stcollectreplyid;
	}
	public void setStcollectreplyid(String stcollectreplyid) {
		this.stcollectreplyid = stcollectreplyid;
	}
	public String getConnected_uid() {
		return connected_uid;
	}
	public void setConnected_uid(String connected_uid) {
		this.connected_uid = connected_uid;
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
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

}
