package cn.fszt.trafficapp.domain;

public class NewsData {

	private String title = "";
	private String hdnewid = "";
	private String newimage = "";
	private String createdate = "";
	private String istop = "";
	private int _id;
	

	public NewsData() {
	}

	public NewsData(int id, String _title, String _hdnewid, String _newimage, String _createdate, String _istop) {
		title = _title;
		hdnewid = _hdnewid;
		newimage = _newimage;
		createdate = _createdate;
		istop = _istop;
		_id = id;
	}

	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHdnewid() {
		return hdnewid;
	}

	public void setHdnewid(String hdnewid) {
		this.hdnewid = hdnewid;
	}

	public String getNewimage() {
		return newimage;
	}

	public void setNewimage(String newimage) {
		this.newimage = newimage;
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

	

	


}
