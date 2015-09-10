package cn.fszt.trafficapp.domain;

/**
 * 节目互动某一个互动的评论数据
 * @author AeiouKong
 *
 */
public class RadioCommentDetailReplyData {

	private String hdcommentreplyid;
	private String hdcommentid;
	private String connected_uid;
	private String nickname;
	private String headimg;
	private String isdj;
	private String isdjpath;
	private String totalscore;
	private String sex;
	private String intro;
	private String content;
	private String imagepath;
	private String voicepath;
	private String createdate;
	
	public String getHdcommentreplyid() {
		return hdcommentreplyid;
	}
	public void setHdcommentreplyid(String hdcommentreplyid) {
		this.hdcommentreplyid = hdcommentreplyid;
	}
	public String getHdcommentid() {
		return hdcommentid;
	}
	public void setHdcommentid(String hdcommentid) {
		this.hdcommentid = hdcommentid;
	}
	public String getTotalscore() {
		return totalscore;
	}
	public void setTotalscore(String totalscore) {
		this.totalscore = totalscore;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
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
