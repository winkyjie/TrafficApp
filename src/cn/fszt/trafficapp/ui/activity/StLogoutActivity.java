package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.oauth.sina.AccessTokenKeeper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 退出登录
 * @author AeiouKong
 *
 */
public class StLogoutActivity extends Activity implements OnClickListener{
	
	private Button btn_lo_logout,btn_cancel;
	private LinearLayout ll_logout;
	private SharedPreferences sp,sp_push;
	private String api_url,uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_logout);
		
		initView();
	}
	
	private void initView(){
		api_url = getString(R.string.api_url);
		btn_lo_logout = (Button) findViewById(R.id.btn_lo_logout);
		btn_lo_logout.setOnClickListener(this);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
		ll_logout.setOnClickListener(this);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		sp_push = getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
	}

	@Override
	public void onClick(View v) {
		if(v == btn_lo_logout){
			new Thread(){
				public void run() {
					logout();
				}
			}.start();
		}
		if(v == btn_cancel){
			finish();
		}
		if(v == ll_logout){
			finish();
		}
	}
	
	//退出登录
	private void logout(){
		
		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "LogoutPersonByUid"));
		params.add(new BasicNameValuePair("connected_uid", uid));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			Log.d("logout_result", result);
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if("200".equals(code)){
					handler.obtainMessage(Constant.LOGOUT_SUCCEED, message).sendToTarget();
				}else{
					handler.obtainMessage(Constant.LOGOUT_FAILED, message).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			handler.sendEmptyMessage(Constant.LOGIN_FAILED_NULL);
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String message = msg.obj+"";
			switch(msg.what){
			case Constant.LOGOUT_SUCCEED:
				setResult(Activity.RESULT_OK);  //通知上级Activity finish();
				//退出登录是否应该清除用户通行证数据
				Editor edit = sp.edit();
				Editor editor = sp_push.edit();
				edit.clear();
				edit.commit();
				editor.clear();
				editor.commit();
				AccessTokenKeeper.clear(StLogoutActivity.this);
				CookieSyncManager.createInstance(StLogoutActivity.this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				cookieManager.removeAllCookie();
				CookieSyncManager.getInstance().sync();
				Intent intent = new Intent(StLogoutActivity.this,StLoginActivity.class);
				startActivity(intent);
				finish();
				break;
			case Constant.LOGOUT_FAILED:
				Toast.makeText(StLogoutActivity.this, message, Toast.LENGTH_SHORT).show();
				break;
			case Constant.LOGOUT_FAILED_NULL:
				Toast.makeText(StLogoutActivity.this, getString(R.string.returndata_null), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
}
