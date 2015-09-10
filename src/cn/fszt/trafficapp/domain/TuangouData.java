package cn.fszt.trafficapp.domain;

public class TuangouData {

	private String hdgroupresultid;
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
	public String getHdgroupresultid() {
		return hdgroupresultid;
	}
	public void setHdgroupresultid(String hdgroupresultid) {
		this.hdgroupresultid = hdgroupresultid;
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
