package cn.fszt.trafficapp.domain;

/**
 * 音频列表
 * @author AeiouKong
 *
 */
public class AudioListData {
	
	private String hdaudiotypeid;
	private String typename;
	private String typedesc;
	private String isusered;
	private String imagepath;
	
	public String getHdaudiotypeid() {
		return hdaudiotypeid;
	}
	public void setHdaudiotypeid(String hdaudiotypeid) {
		this.hdaudiotypeid = hdaudiotypeid;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getTypedesc() {
		return typedesc;
	}
	public void setTypedesc(String typedesc) {
		this.typedesc = typedesc;
	}
	public String getIsusered() {
		return isusered;
	}
	public void setIsusered(String isusered) {
		this.isusered = isusered;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	
}
