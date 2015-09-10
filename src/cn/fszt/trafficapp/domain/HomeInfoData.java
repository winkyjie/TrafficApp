package cn.fszt.trafficapp.domain;

import java.io.Serializable;

public class HomeInfoData implements Serializable{
	
	/**
	 * 首页跑马灯数据
	 */
	private static final long serialVersionUID = 1L;
	private String hdinfoid;
	private String title;
	private String hdtype;
	private String newimage;
	
	
	public String getNewimage() {
		return newimage;
	}
	public void setNewimage(String newimage) {
		this.newimage = newimage;
	}
	public String getHdinfoid() {
		return hdinfoid;
	}
	public void setHdinfoid(String hdinfoid) {
		this.hdinfoid = hdinfoid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHdtype() {
		return hdtype;
	}
	public void setHdtype(String hdtype) {
		this.hdtype = hdtype;
	}

}
