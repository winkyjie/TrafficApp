package cn.fszt.trafficapp.domain;

/**
 * 点播室-选择节目group
 * @author AeiouKong
 *
 */
public class ReplaysData {
	private String hdreplaytypeid = "";
	private String typename = "";
	private String imagepath = "";
	private String livetime = "";
	private String djname = "";
	private String imagebackground = "";
	private int _id;
	
	private boolean visiable;
	
	public boolean isVisiable() {
		return visiable;
	}

	public String getImagebackground() {
		return imagebackground;
	}

	public void setImagebackground(String imagebackground) {
		this.imagebackground = imagebackground;
	}

	public void setVisiable(boolean visiable) {
		this.visiable = visiable;
	}

	public ReplaysData(){
	}
	
	public ReplaysData(int _id, String hdreplaytypeid, String typename,
			String imagepath, String livetime, String djname) {
		this.hdreplaytypeid = hdreplaytypeid;
		this.typename = typename;
		this.imagepath = imagepath;
		this.livetime = livetime;
		this.djname = djname;
		this._id = _id;
	}

	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getHdreplaytypeid() {
		return hdreplaytypeid;
	}
	public void setHdreplaytypeid(String hdreplaytypeid) {
		this.hdreplaytypeid = hdreplaytypeid;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getLivetime() {
		return livetime;
	}
	public void setLivetime(String livetime) {
		this.livetime = livetime;
	}
	public String getDjname() {
		return djname;
	}
	public void setDjname(String djname) {
		this.djname = djname;
	}
	
}
