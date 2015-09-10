package cn.fszt.trafficapp.domain;

public class VoteData {

	private String hdpersonid;
	private String hdvoteid;
	private String hdvotelogid;
	private String remark;
	private String createdate;
	private String connected_uid;
	
	public String getConnected_uid() {
		return connected_uid;
	}
	public void setConnected_uid(String connected_uid) {
		this.connected_uid = connected_uid;
	}
	public String getHdpersonid() {
		return hdpersonid;
	}
	public void setHdpersonid(String hdpersonid) {
		this.hdpersonid = hdpersonid;
	}
	public String getHdvoteid() {
		return hdvoteid;
	}
	public void setHdvoteid(String hdvoteid) {
		this.hdvoteid = hdvoteid;
	}
	public String getHdvotelogid() {
		return hdvotelogid;
	}
	public void setHdvotelogid(String hdvotelogid) {
		this.hdvotelogid = hdvotelogid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	
}
