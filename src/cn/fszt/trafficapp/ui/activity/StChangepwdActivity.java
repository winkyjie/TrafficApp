package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.umeng.analytics.MobclickAgent;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Base64Util;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 用手机登录的情况下修改密码
 * 
 * @author AeiouKong
 *
 */
public class StChangepwdActivity extends BaseBackActivity implements
		OnClickListener {

	private EditText et_cha_user, et_cha_oldpwd, et_cha_newpwd;
	private ImageView btn_cha_submit;
	private ProgressDialog pd;
	private String api_url;

	private SharedPreferences sp;

	private String mobile, uid;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_changepwd);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.changepwd));
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
		et_cha_user = (EditText) findViewById(R.id.et_cha_user);
		et_cha_oldpwd = (EditText) findViewById(R.id.et_cha_oldpwd);
		et_cha_newpwd = (EditText) findViewById(R.id.et_cha_newpwd);
		btn_cha_submit = (ImageView) findViewById(R.id.btn_cha_submit);
		btn_cha_submit.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);

		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		mobile = sp.getString("mobile", null);
		et_cha_user.setText(mobile);
		uid = sp.getString("uuid", null);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_cha_submit) {
			final String oldowd = et_cha_oldpwd.getText().toString().trim();
			final String newpwd = et_cha_newpwd.getText().toString().trim();
			if (!mobile.equals("") && !oldowd.equals("") && !newpwd.equals("")
					&& newpwd.length() >= 6 && newpwd.length() <= 12) {
				pd.show();
				new Thread() {
					public void run() {
						change_password(oldowd, newpwd);
					}
				}.start();
			} else {
				showMsg(getString(R.string.input_wrong), "alert", "top");
			}
		}
	}

	private void change_password(String oldpwd, String newpwd) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "updatepwd"));
		params.add(new BasicNameValuePair("userid", uid));
		oldpwd = Base64Util.encode64(oldpwd);
		newpwd = Base64Util.encode64(newpwd);
		params.add(new BasicNameValuePair("oldpwd", oldpwd));
		params.add(new BasicNameValuePair("newpwd", newpwd));

		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			Log.d("changepwd_result", result);
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.CHANGEPWD_SUCCEED, message)
							.sendToTarget();
				} else {
					handler.obtainMessage(Constant.CHANGEPWD_FAILED, message)
							.sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.CHANGEPWD_FAILED_NULL);
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			pd.dismiss();
			String message = msg.obj + "";
			switch (msg.what) {
			case Constant.CHANGEPWD_SUCCEED:
				// 隐藏软键盘
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}
				Intent intent = getIntent();
				intent.putExtra("message", message);
				setResult(RESULT_OK, intent); // 通知上级Activity
				finish();
				break;
			case Constant.CHANGEPWD_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.CHANGEPWD_FAILED_NULL:
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

		appMsg = AppMsg.makeText(StChangepwdActivity.this, msg, style);

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
