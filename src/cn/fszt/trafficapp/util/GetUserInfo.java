package cn.fszt.trafficapp.util;

import com.google.gson.Gson;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.UserInfoData;
import android.content.Context;
import android.content.SharedPreferences;

public class GetUserInfo {
	
	private static SharedPreferences sp;
	private static String getinfo_url,uid;
	private static UserInfoData user;
	/**
	 * 获取当前用户的信息
	 * @return
	 */
	public static UserInfoData userinfo(Context context){
		user = new UserInfoData();
		sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);                                                                                                                           
		uid = sp.getString("uuid", null);
		getinfo_url = context.getString(R.string.server_url)+context.getString(R.string.getuserinfo_url)+"&connected_uid=";
		String result = HttpUtil.GetStringFromUrl(getinfo_url+uid);
		if(result!=null){
			Gson gson = new Gson();
	        user = gson.fromJson(result, UserInfoData.class);
	        return user;
		}else{
			return null;
		}
	}
}
