package cn.fszt.trafficapp.domain;

public class CarInfoData {

	private String hdpersoncarid;	//用户车资料表主键
	private String connected_uid;	//用户的connected_uid
	private String carnum;			//车牌号
	private String cartypecode;		//车型代码
	private String lastfour;		//发动机号或车架号后四位
	private String createdate;		//创建日期
	public String getHdpersoncarid() {
		return hdpersoncarid;
	}
	public void setHdpersoncarid(String hdpersoncarid) {
		this.hdpersoncarid = hdpersoncarid;
	}
	public String getConnected_uid() {
		return connected_uid;
	}
	public void setConnected_uid(String connected_uid) {
		this.connected_uid = connected_uid;
	}
	public String getCarnum() {
		return carnum;
	}
	public void setCarnum(String carnum) {
		this.carnum = carnum;
	}
	public String getCartypecode() {
		return cartypecode;
	}
	public void setCartypecode(String cartypecode) {
		this.cartypecode = cartypecode;
	}
	public String getLastfour() {
		return lastfour;
	}
	public void setLastfour(String lastfour) {
		this.lastfour = lastfour;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	
}
