package cn.fszt.trafficapp.domain;

import java.io.Serializable;
import java.util.List;

public class ShareDetailData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String connected_uid;
	private String nickname;
	private String headimg;
	private String isdj;
	private String isdjpath;
	private String intro;
	private String content;
	private String imagepath;
	private String imagepath_small;
	private String videopath;
	private String createdate;
	private String stcollectid;
	private String replycount;
	private String likecount;
	private String istransmit;
	private String transmiturl;
	
	private String istop;
	private String clienttype;
	private String clientno;
	private String totalscore;
	private String sex;
	
	private List replydetail;
	
	public List getReplydetail() {
		return replydetail;
	}
	public void setReplydetail(List replydetail) {
		this.replydetail = replydetail;
	}
	public String getIstop() {
		return istop;
	}
	public void setIstop(String istop) {
		this.istop = istop;
	}
	public String getClienttype() {
		return clienttype;
	}
	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
	}
	public String getClientno() {
		return clientno;
	}
	public void setClientno(String clientno) {
		this.clientno = clientno;
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
	public String getTransmiturl() {
		return transmiturl;
	}
	public void setTransmiturl(String transmiturl) {
		this.transmiturl = transmiturl;
	}
	public String getIstransmit() {
		return istransmit;
	}
	public void setIstransmit(String istransmit) {
		this.istransmit = istransmit;
	}
	public String getImagepath_small() {
		return imagepath_small;
	}
	public void setImagepath_small(String imagepath_small) {
		this.imagepath_small = imagepath_small;
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
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getStcollectid() {
		return stcollectid;
	}
	public void setStcollectid(String stcollectid) {
		this.stcollectid = stcollectid;
	}
	public String getReplycount() {
		return replycount;
	}
	public void setReplycount(String replycount) {
		this.replycount = replycount;
	}
	public String getLikecount() {
		return likecount;
	}
	public void setLikecount(String likecount) {
		this.likecount = likecount;
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
	public String getVideopath() {
		return videopath;
	}
	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}


}
