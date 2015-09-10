package cn.fszt.trafficapp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

public class UserUtil {

	/**
	 * 下发短信
	 * @param method   
	 * 			login: 登录
	 *			forget：忘记密码
	 *			register：注册
	 *			changemobile：变更手机号码
     *
	 * @param mobile
	 * @param handler
	 * @param url
	 */
	public static void sendsms(String method,String mobile,Handler handler,String url){
		
		JSONObject jsonObject;
		List <NameValuePair> params=new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("action","sendsms"));
	    params.add(new BasicNameValuePair("method",method));
	    params.add(new BasicNameValuePair("mobile",mobile));
	    String result = HttpUtil.PostStringFromUrl(url, params);
//	    System.out.println("sendsms_result==="+result);
	    if(result!=null){
	    	try {
	    		jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if("200".equals(code)){
					handler.obtainMessage(Constant.SENDSMS_SUCCEED, message).sendToTarget();
				}else{
					handler.obtainMessage(Constant.SENDSMS_FAILED, message).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }else{
	    	handler.sendEmptyMessage(Constant.SENDSMS_FAILED_NULL);
	    }
	}
}
