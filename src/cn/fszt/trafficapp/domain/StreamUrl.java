package cn.fszt.trafficapp.domain;

public class StreamUrl {

	private String id = "";
	private String iphone_stream_url = "";
	private String android_stream_url = "";
	
	
	public StreamUrl(){
	}
	
	public StreamUrl(String _id,String _iphone_stream_url,String _android_stream_url){
	
		id = _id;
		iphone_stream_url = _iphone_stream_url;
		android_stream_url = _android_stream_url;
	}
	
	@Override
	public String toString() { 

		return id+","+iphone_stream_url+","+android_stream_url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIphone_stream_url() {
		return iphone_stream_url;
	}

	public void setIphone_stream_url(String iphone_stream_url) {
		this.iphone_stream_url = iphone_stream_url;
	}

	public String getAndroid_stream_url() {
		return android_stream_url;
	}

	public void setAndroid_stream_url(String android_stream_url) {
		this.android_stream_url = android_stream_url;
	}
	
	
}
