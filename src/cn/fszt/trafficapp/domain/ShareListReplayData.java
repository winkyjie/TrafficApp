package cn.fszt.trafficapp.domain;

import java.io.Serializable;

public class ShareListReplayData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String reply_nickname;
	private String reply_headimg;
	private String reply_content;
	
	public String getReply_nickname() {
		return reply_nickname;
	}
	public void setReply_nickname(String reply_nickname) {
		this.reply_nickname = reply_nickname;
	}
	public String getReply_headimg() {
		return reply_headimg;
	}
	public void setReply_headimg(String reply_headimg) {
		this.reply_headimg = reply_headimg;
	}
	public String getReply_content() {
		return reply_content;
	}
	public void setReply_content(String reply_content) {
		this.reply_content = reply_content;
	}
	
}
