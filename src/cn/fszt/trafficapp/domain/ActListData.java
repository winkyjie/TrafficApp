package cn.fszt.trafficapp.domain;

/**
 * 精选活动、车生活列表数据
 * @author AeiouKong
 *
 */
public class ActListData {

	private int _id;
	private String title = "";
	private String hdinfoid = "";
	private String newimage = "";
	private String createdate = "";
	private String commentcount = "";
	private String views = "";
	

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHdinfoid() {
		return hdinfoid;
	}

	public void setHdinfoid(String hdinfoid) {
		this.hdinfoid = hdinfoid;
	}

	public String getNewimage() {
		return newimage;
	}

	public void setNewimage(String newimage) {
		this.newimage = newimage;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

}
