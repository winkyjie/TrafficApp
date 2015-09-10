package cn.fszt.trafficapp.domain;

/**
 * 报料排行榜
 * @author AeiouKong
 *
 */
public class BaoliaoSortData {

	private String nickname = "";
	private String num = "";
	private String headimg = "";
	private String recordcount = "";
	
	
	public BaoliaoSortData(String nickname, String num, String headimg,
			String recordcount) {
		super();
		this.nickname = nickname;
		this.num = num;
		this.headimg = headimg;
		this.recordcount = recordcount;
	}
	
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getRecordcount() {
		return recordcount;
	}
	public void setRecordcount(String recordcount) {
		this.recordcount = recordcount;
	}
	

	



}
