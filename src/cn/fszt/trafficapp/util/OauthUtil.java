package cn.fszt.trafficapp.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.fszt.trafficapp.domain.OauthUsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class OauthUtil {

	/**
	 * 获取通行证信息，用户手机/邮箱/qq/sina的绑定、验证状态，昵称
	 * @return
	 */
	public static List<OauthUsers> getDataFromNetwork(Context context,String request_url,String auth_code,int uid){
		JSONObject jsonObject;
		JSONArray oauth_users = null;
		String str_oauth = null;
		List<OauthUsers> lst = new ArrayList<OauthUsers>();
		String nickname = null;
		String avatar = null;
		String validated = null;
		SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		
		List <NameValuePair> params=new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("api","get_all_connected_oauth_users"));
	    params.add(new BasicNameValuePair("version","1.0"));
	    params.add(new BasicNameValuePair("uid",uid+""));
	    params.add(new BasicNameValuePair("auth_code",auth_code));
		String result = HttpUtil.PostStringFromUrl(request_url, params);
		if(result!=null){
			try {
				jsonObject = new JSONObject(result);
				nickname = jsonObject.getString("username");
				avatar = jsonObject.getString("avatar");
				validated = jsonObject.getString("validated");
				edit.putString("nickname", nickname);
				edit.putString("headimg", avatar);
				edit.putString("validate", validated);
				edit.commit();
				oauth_users = jsonObject.getJSONArray("oauth_users");
				str_oauth = oauth_users.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(str_oauth != null){
			Type listType = new TypeToken<List<OauthUsers>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(oauth_users.toString(), listType);
	        return lst;
		}else{
			return null;
		}
	}
}
