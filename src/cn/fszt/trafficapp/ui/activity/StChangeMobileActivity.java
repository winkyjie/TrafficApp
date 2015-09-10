package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.UserUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 变更手机号码
 * 
 * @author AeiouKong
 *
 */
public class StChangeMobileActivity extends BaseBackActivity implements OnClickListener {

	private EditText et_cha_user, et_cha_val;
	private Button btn_cha_val;
	private ImageView btn_cha_submit;
	private ProgressDialog pd;
	private String api_url,uid,mobile,tpye;
	private SharedPreferences sp;
	private Intent intent;
	private int time = Constant.VALIDATECODE_TIME;
	private Timer timer;
	private TimerTask task;
	private Editor edit;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_changemobile);

		initView();

		if("bind".equals(tpye)){
			getActionBar().setTitle(getString(R.string.bindmobile));
		}else if("change".equals(tpye)){
			getActionBar().setTitle(getString(R.string.changemobile));
		}
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		intent = getIntent();
		tpye = intent.getStringExtra("tpye");
		api_url = getString(R.string.api_url);
		et_cha_user = (EditText) findViewById(R.id.et_cha_user);
		et_cha_val = (EditText) findViewById(R.id.et_cha_val);
		btn_cha_val = (Button) findViewById(R.id.btn_cha_val);
		btn_cha_val.setOnClickListener(this);
		btn_cha_submit = (ImageView) findViewById(R.id.btn_cha_submit);
		btn_cha_submit.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		edit = sp.edit();
	}

	@Override
	public void onClick(View v) {
		if (v == btn_cha_submit) {
			
			mobile = et_cha_user.getText().toString().trim();
			final String val = et_cha_val.getText().toString().trim();
			if (!mobile.equals("") && !val.equals("")) {
				String f_mobile = getString(R.string.mobile_matches);
				if (mobile.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							changemobile(mobile,val,uid);
						}
					}.start();

				} else {
					showMsg(getString(R.string.inputmobileoremail), "alert",
							"top");
				}
			} else {
				showMsg(getString(R.string.input_wrong), "alert", "top");
			}
		}
		
		if (v == btn_cha_val) {
			// 获取验证码
			time = Constant.VALIDATECODE_TIME;
			timer = new Timer(true);
			final String mobile = et_cha_user.getText().toString().trim();
			if (!mobile.equals("")) {
				String f_mobile = getString(R.string.mobile_matches);
				if (mobile.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							UserUtil.sendsms("changemobile", mobile, handler, api_url);
						}
					}.start();
				} else {
					showMsg(getString(R.string.inputmobileoremail), "alert",
							"top");
				}
			} else {
				showMsg(getString(R.string.input_wrong), "alert", "top");
			}
		}
	}

	private void changemobile(String mobile, String smsauthcode, String userid) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "changemobile"));
		params.add(new BasicNameValuePair("userid", userid));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("smsauthcode", smsauthcode));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			Log.d("result_changemobile", result);
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.CHANGEMOBILE_SUCCEED, message)
							.sendToTarget();
				} else {
					handler.obtainMessage(Constant.CHANGEMOBILE_FAILED, message)
							.sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.CHANGEMOBILE_FAILED_NULL);
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			String message = (String) msg.obj;
			pd.dismiss();
			switch (msg.what) {
			case Constant.CHANGEMOBILE_SUCCEED:
				//隐藏软键盘
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		        }
				Intent intent = getIntent();
				intent.putExtra("message", message);
				intent.putExtra("mobile", mobile);
				//TODO
				setResult(RESULT_OK,intent);
				edit.putString("mobile", mobile);
				edit.commit();
				finish();
				break;
			case Constant.CHANGEMOBILE_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.CHANGEMOBILE_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.SENDSMS_SUCCEED:
				showMsg(message, "alert", "top");
				btn_cha_val.setEnabled(false);
				btn_cha_val.setBackgroundResource(R.drawable.res_blank_val);
				task = new TimerTask() {
					public void run() {
						handler.sendEmptyMessage(Constant.VALIDATECODE_UI);
					}
				};
				timer.schedule(task, 0, 1000);
				break;
			case Constant.SENDSMS_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.SENDSMS_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.VALIDATECODE_UI:
				time--;
				btn_cha_val.setText(time
						+ getResources().getString(R.string.regetvalidateno));
				if (time == 0) {
					task.cancel();
					task = null;
					timer.cancel();
					timer = null;
					btn_cha_val.setText("");
					btn_cha_val.setEnabled(true);
					btn_cha_val.setBackgroundResource(R.drawable.btn_res_val);
				}
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

		appMsg = AppMsg.makeText(StChangeMobileActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
