package cn.fszt.trafficapp.domain;

/**
 * 我的消息--最新通知列表
 * @author AeiouKong
 *
 */
public class MsglistData {

	private String msgid;	
	private String msgtitle;
	private String msgtype;
	private String starttime;
	
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getMsgtitle() {
		return msgtitle;
	}
	public void setMsgtitle(String msgtitle) {
		this.msgtitle = msgtitle;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
}
