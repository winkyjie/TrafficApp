package cn.fszt.trafficapp.util;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.fszt.trafficapp.R;
import android.app.Activity;

public class GetBlackList {

	private String getblacklist_url;
	public static final int ISBLACKLIST = 00;
	public static final int NOTBLACKLIST = 01;
	
	public GetBlackList(Activity act,int uid){
		getblacklist_url = act.getString(R.string.server_url)+act.getString(R.string.getblacklist_url)+"&connected_uid="+uid;
	}
	
	public HashMap<String,String> getblacklist(){
		
		String result = HttpUtil.GetStringFromUrl(getblacklist_url);
		HashMap<String,String> map = new HashMap<String,String>();
		if(result!=null){
			try {
				JSONObject jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String isblacklist = jsonObject.getString("isblacklist");
				String message = jsonObject.getString("message");
				if(code!=null&&code.equals("200")){
					map.put("isblacklist", isblacklist);
					map.put("message", message);
					return map;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
		return map;
	}
}
