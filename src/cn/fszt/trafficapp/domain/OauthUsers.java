package cn.fszt.trafficapp.domain;

public class OauthUsers {

	private String auth_time = "";
	private String expires_in = "";
	private String openid = "";
	private String access_token = "";
	private String validate_time = "";
	private String site = "";
	
	
	public String getAuth_time() {
		return auth_time;
	}
	public void setAuth_time(String auth_time) {
		this.auth_time = auth_time;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getValidate_time() {
		return validate_time;
	}
	public void setValidate_time(String validate_time) {
		this.validate_time = validate_time;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
	
}
