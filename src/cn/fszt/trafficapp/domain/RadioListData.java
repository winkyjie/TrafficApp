package cn.fszt.trafficapp.domain;

/**
 * 听广播--节目时表
 * @author AeiouKong
 * 2015-5-14
 */
public class RadioListData {

	private String hdmenuid;
	private String week;
	private String starttime;
	private String endtime;
	private String programname;
	private String host;
	private String imagepath;
	private String imagebackground;
	
	public String getHdmenuid() {
		return hdmenuid;
	}
	public void setHdmenuid(String hdmenuid) {
		this.hdmenuid = hdmenuid;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getProgramname() {
		return programname;
	}
	public void setProgramname(String programname) {
		this.programname = programname;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getImagebackground() {
		return imagebackground;
	}
	public void setImagebackground(String imagebackground) {
		this.imagebackground = imagebackground;
	}

}
