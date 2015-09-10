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
import cn.fszt.trafficapp.util.Base64Util;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.UserUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 忘记密码，在没有登录的情况下
 * 
 * @author AeiouKong
 *
 */
public class StResetpwdActivity extends BaseBackActivity implements
		OnClickListener {

	private EditText et_res_user, et_res_val, et_res_newpwd;
	private Button btn_res_val;
	private ImageView btn_res_submit;
	private ProgressDialog pd;
	private String api_url;

	private int time = Constant.VALIDATECODE_TIME;
	private Timer timer;
	private TimerTask task;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resetpwd);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.resetpwd));
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
		api_url = getString(R.string.api_url);
		et_res_user = (EditText) findViewById(R.id.et_res_user);
		et_res_val = (EditText) findViewById(R.id.et_res_val);
		et_res_newpwd = (EditText) findViewById(R.id.et_res_newpwd);
		btn_res_val = (Button) findViewById(R.id.btn_res_val);
		btn_res_val.setOnClickListener(this);
		btn_res_submit = (ImageView) findViewById(R.id.btn_res_submit);
		btn_res_submit.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_res_submit) {
			// 提交重设密码
			final String mobile = et_res_user.getText().toString().trim();
			final String val = et_res_val.getText().toString().trim();
			final String newpwd = et_res_newpwd.getText().toString().trim();
			if (!mobile.equals("") && !val.equals("") && !newpwd.equals("")
					&& newpwd.length() >= 6 && newpwd.length() <= 12) {
				// 执行重设密码
				String f_mobile = getString(R.string.mobile_matches);
				if (mobile.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							resetpwd(mobile, val, newpwd);
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
		if (v == btn_res_val) {
			// 获取验证码
			time = Constant.VALIDATECODE_TIME;
			timer = new Timer(true);
			final String mobile = et_res_user.getText().toString().trim();
			if (!mobile.equals("")) {
				String f_mobile = getString(R.string.mobile_matches);
				if (mobile.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							UserUtil.sendsms("forget", mobile, handler, api_url);
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

	private void resetpwd(String mobile, String smsauthcode, String newpwd) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "forgetpwd"));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("smsauthcode", smsauthcode));
		newpwd = Base64Util.encode64(newpwd);
		params.add(new BasicNameValuePair("newpwd", newpwd));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			Log.d("result_resetpwd", result);
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.RESETPWD_SUCCEED, message)
							.sendToTarget();
				} else {
					handler.obtainMessage(Constant.RESETPWD_FAILED, message)
							.sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.RESETPWD_FAILED_NULL);
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			String message = (String) msg.obj;
			pd.dismiss();
			switch (msg.what) {
			case Constant.RESETPWD_SUCCEED:
				showMsg(message, "alert", "top");
				finish();
				break;
			case Constant.RESETPWD_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.RESETPWD_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.SENDSMS_SUCCEED:
				showMsg(message, "alert", "top");
				btn_res_val.setEnabled(false);
				btn_res_val.setBackgroundResource(R.drawable.res_blank_val);
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
				btn_res_val.setText(time
						+ getResources().getString(R.string.regetvalidateno));
				if (time == 0) {
					task.cancel();
					task = null;
					timer.cancel();
					timer = null;
					btn_res_val.setText("");
					btn_res_val.setEnabled(true);
					btn_res_val.setBackgroundResource(R.drawable.btn_res_val);
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

		appMsg = AppMsg.makeText(StResetpwdActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

}
