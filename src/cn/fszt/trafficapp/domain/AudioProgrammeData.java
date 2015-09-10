package cn.fszt.trafficapp.domain;

/**
 * 音频节目专辑列表
 * 
 * @author AeiouKong
 *
 */
public class AudioProgrammeData {

	private String hdaudioid;
	private String hdaudiotypeid;
	private String title;
	private String voicepath;
	private String imagepath;
	private String createdate;
	private String playcount;
	private boolean playing;
	private String filepath = null;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public String getPlaycount() {
		return playcount;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public String getHdaudioid() {
		return hdaudioid;
	}

	public void setHdaudioid(String hdaudioid) {
		this.hdaudioid = hdaudioid;
	}

	public String getHdaudiotypeid() {
		return hdaudiotypeid;
	}

	public void setHdaudiotypeid(String hdaudiotypeid) {
		this.hdaudiotypeid = hdaudiotypeid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVoicepath() {
		return voicepath;
	}

	public void setVoicepath(String voicepath) {
		this.voicepath = voicepath;
	}

	public String getImagepath() {
		return imagepath;
	}

	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

}
