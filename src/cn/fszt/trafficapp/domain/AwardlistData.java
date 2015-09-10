package cn.fszt.trafficapp.domain;

/**
 * 我的消息--中奖通知
 * @author AeiouKong
 *
 */
public class AwardlistData {
	
	private String hdawardid;
	private String mobile;
	private String nickname;
	private String awardcontent;
	private String createtime;
	
	public String getHdawardid() {
		return hdawardid;
	}
	public void setHdawardid(String hdawardid) {
		this.hdawardid = hdawardid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAwardcontent() {
		return awardcontent;
	}
	public void setAwardcontent(String awardcontent) {
		this.awardcontent = awardcontent;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

}
