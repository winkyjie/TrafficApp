package cn.fszt.trafficapp.domain;

public class BaomingData {

	private String hdactivityresultid;
	private String hdinfoid;
	private String remark;
	private String createdate;
	private String connected_uid;
	
	public String getConnected_uid() {
		return connected_uid;
	}
	public void setConnected_uid(String connected_uid) {
		this.connected_uid = connected_uid;
	}
	public String getHdactivityresultid() {
		return hdactivityresultid;
	}
	public void setHdactivityresultid(String hdactivityresultid) {
		this.hdactivityresultid = hdactivityresultid;
	}
	public String getHdinfoid() {
		return hdinfoid;
	}
	public void setHdinfoid(String hdinfoid) {
		this.hdinfoid = hdinfoid;
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
