package cn.fszt.trafficapp.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import cn.fszt.trafficapp.R;

public class AppClickCount {

	private String api_url;
	private String functionName;
	private SharedPreferences sp_user;
	private String uid;

	public AppClickCount(Context context, String functionName) {
		this.functionName = functionName;
		api_url = context.getResources().getString(R.string.api_url);
		sp_user = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp_user.getString("uuid", "");
		InsertHdfnclickdetail();
	}

	public void InsertHdfnclickdetail() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "InsertHdfnclickdetail"));
				params.add(new BasicNameValuePair("connected_uid", uid));
				params.add(new BasicNameValuePair("apptype", "android"));
				params.add(new BasicNameValuePair("functionname", functionName));
				HttpUtil.PostStringFromUrl(api_url, params);
			}
		}).start();
	}

}
