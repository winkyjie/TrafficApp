package cn.fszt.trafficapp.domain;

/**
 * 路况信息
 * @author AeiouKong
 *
 */
public class TextData {

	private int _id;
	private String address = "";
	private String event = "";
	private String createtime = "";
	private String remark = "";
	

	public TextData() {
	}

	public TextData(int id,String _address, String _event, String _createtime, String _remark) {
		address = _address;
		event = _event;
		createtime = _createtime;
		remark = _remark;
		_id = id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}


}
