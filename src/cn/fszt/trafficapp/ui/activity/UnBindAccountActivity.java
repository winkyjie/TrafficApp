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
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class UnBindAccountActivity extends BaseBackActivity implements OnClickListener{
	
	private ImageView iv_unbind;
	private TextView tv_name;
	private String api_url,uid,name;
	private Intent intent;
	private SharedPreferences sp;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_unbindaccount);
		
		initView();
		
		getActionBar().setTitle("QQ");
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void initView() {
		intent = getIntent();
		name = intent.getStringExtra("name");
		api_url = getString(R.string.api_url);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(name);
		iv_unbind = (ImageView) findViewById(R.id.iv_unbind);
		iv_unbind.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		
		if(v == iv_unbind){
			new Thread(){
				public void run() {
					unbind(uid,"qq");
				}
			}.start();
		}
	}
	
	private void unbind(String userid,String opentype){
		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "unbindopenid"));
		params.add(new BasicNameValuePair("userid", userid));
		params.add(new BasicNameValuePair("opentype", opentype));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if("200".equals(code)){
					handler.obtainMessage(Constant.UNBIND_SUCCEED, message).sendToTarget();
				}else{
					handler.obtainMessage(Constant.UNBIND_FAILED, message).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			String message = (String) msg.obj;
			
			switch(msg.what){
			case Constant.UNBIND_SUCCEED:
				Intent intent = getIntent();
				intent.putExtra("message", message);
				setResult(RESULT_OK,intent);
				finish();
			break;
			case Constant.UNBIND_FAILED:
				showMsg(message, "alert", "top");
			break;
			case Constant.UNBIND_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
			break;
			}
		}
	};
	
	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(UnBindAccountActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}
}
