package cn.fszt.trafficapp.domain;

public class ReplaysItemData {
	private int _id;
	private String hdreplaytypeid = "";
	private String hdreplayid = "";
	private String imagepath = "";
	private String title = "";
	private String voicepath = "";
	private String createdate = "";
	private String replaycount = "";
	
	public ReplaysItemData(){
		
	}
	
	public ReplaysItemData(int _id, String hdreplaytypeid, String hdreplayid,
			String imagepath, String title, String voicepath, String createdate) {
		super();
		this._id = _id;
		this.hdreplaytypeid = hdreplaytypeid;
		this.hdreplayid = hdreplayid;
		this.imagepath = imagepath;
		this.title = title;
		this.voicepath = voicepath;
		this.createdate = createdate;
	}
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public String getReplaycount() {
		return replaycount;
	}

	public void setReplaycount(String replaycount) {
		this.replaycount = replaycount;
	}

	public String getHdreplaytypeid() {
		return hdreplaytypeid;
	}
	public void setHdreplaytypeid(String hdreplaytypeid) {
		this.hdreplaytypeid = hdreplaytypeid;
	}
	public String getHdreplayid() {
		return hdreplayid;
	}
	public void setHdreplayid(String hdreplayid) {
		this.hdreplayid = hdreplayid;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
