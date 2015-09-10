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
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 从@UserInfoActivity 和 @UserValidate 跳转过来的验证页面
 * 
 * @author AeiouKong
 *
 */
public class StValidateActivity extends BaseBackActivity implements
		OnClickListener {

	private EditText et_val_user, et_val_val;
	private Button btn_val_val;
	private ImageView btn_val_submit;
	private ProgressDialog pd;
	private String passport_url, setuserouth_url;
	private static final int _SENDRESET_FAILED = 1;
	private static final int SENDRESET_SECCEED = 2;
	private static final int SENDRESET_FAILD = 3;

	private static final int _RESET_FAILED = 5;
	private static final int RESET_SECCEED = 6;
	private static final int RESET_FAILD = 7;

	private static final int PROGRESSDIALOG = 4;
	private static final int _PROGRESSDIALOG = 8;

	private SharedPreferences sp;

	private int time = 90;

	private Timer timer;
	private TimerTask task;

	private String user, usertype, authapi;
	private int uid;
	private String auth_code;

	private boolean newregister;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_validate);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.validate));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			backdialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		passport_url = getResources().getString(R.string.passport);
		setuserouth_url = getString(R.string.server_url)
				+ getString(R.string.setuserouth_url);
		et_val_user = (EditText) findViewById(R.id.et_val_user);
		et_val_val = (EditText) findViewById(R.id.et_val_val);
		btn_val_val = (Button) findViewById(R.id.btn_val_val);
		btn_val_val.setOnClickListener(this);
		btn_val_submit = (ImageView) findViewById(R.id.btn_val_submit);
		btn_val_submit.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);

		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getInt("uid", 0);
		auth_code = sp.getString("auth_code", null);

		Intent intent = getIntent();

		usertype = intent.getStringExtra("usertype");
		authapi = intent.getStringExtra("authapi");
		if (usertype.equals("email")) {
			user = intent.getStringExtra("email");
			et_val_user.setText(user);
		} else if (usertype.equals("mobile")) {
			user = intent.getStringExtra("mobile");
			et_val_user.setText(user);
		}
		newregister = intent.getBooleanExtra("newregister", false);
		if (newregister) {
			send_verify();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_val_submit) {
			// 提交验证
			final String val = et_val_val.getText().toString().trim();
			if (!user.equals("") && !val.equals("")) {
				// 执行验证
				String f_email = getResources().getString(
						R.string.email_matches);
				String f_mobile = getResources().getString(
						R.string.mobile_matches);
				// if(user.matches(f_email)){
				// pd.show();
				// new Thread(){
				// public void run() {
				// validate("validate_email",val);
				// handler.sendEmptyMessage(PROGRESSDIALOG);
				// }
				// }.start();
				//
				// }else

				if (user.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							validate("validate_mobile", val);
						}
					}.start();

				} else {
					showMsg(getResources().getString(
							R.string.inputmobileoremail), "alert", "top");
				}
			} else if (user.equals("")) {
				showMsg(getResources().getString(R.string.inputmobileoremail),
						"alert", "top");
			} else if (val.equals("")) {
				showMsg(getResources().getString(R.string.inputvalidate),
						"alert", "top");
			} else {
				showMsg(getResources().getString(R.string.inputuserinfo),
						"alert", "top");
			}
		}
		if (v == btn_val_val) {
			send_verify();
		}
	}

	// 获取验证码
	private void send_verify() {

		time = 90;
		timer = new Timer(true);
		if (!user.equals("")) {
			String f_email = getResources().getString(R.string.email_matches);
			String f_mobile = getResources().getString(R.string.mobile_matches);
			// if(user.matches(f_email)){
			// pd.show();
			// new Thread(){
			// public void run() {
			// send_verify("send_email_verify_code");
			// handler.sendEmptyMessage(_PROGRESSDIALOG);
			// }
			// }.start();
			//
			// }else
			if (user.matches(f_mobile)) {
				pd.show();
				new Thread() {
					public void run() {
						send_verify("send_mobile_verify_code");
						handler.sendEmptyMessage(_PROGRESSDIALOG);
					}
				}.start();

			} else {
				showMsg(getResources().getString(R.string.inputmobileoremail),
						"alert", "top");
			}
		} else if (user.equals("")) {
			showMsg(getResources().getString(R.string.inputmobileoremail),
					"alert", "top");
		}
	}

	// 请求接口发送验证码短信
	private void send_verify(String api) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api", api));
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("auth_code", auth_code));
		String result = HttpUtil.PostStringFromUrl(passport_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				if ("200".equals(code)) {
					boolean send_result = jsonObject.getBoolean("send_result");
					if (send_result) {
						handler.sendEmptyMessage(SENDRESET_SECCEED);
					} else {
						handler.sendEmptyMessage(SENDRESET_FAILD);
					}
				} else if ("500".equals(code)) {
					String message = jsonObject.getString("message");
					handler.obtainMessage(_SENDRESET_FAILED, message)
							.sendToTarget();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void validate(String api, String verify_code) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api", api));
		params.add(new BasicNameValuePair("version", "1.0"));
		params.add(new BasicNameValuePair("verify_code", verify_code));
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("auth_code", auth_code));
		String result = HttpUtil.PostStringFromUrl(passport_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				if ("200".equals(code)) {
					boolean validate_result = jsonObject
							.getBoolean("validate_result");
					if (validate_result) {
						// TODO
						String result_outh = HttpUtil
								.GetStringFromUrl(setuserouth_url
										+ "&connected_uid=" + uid);
						if (result_outh != null) {
							jsonObject = new JSONObject(result_outh);
							String code_outh = jsonObject.getString("code");
							if (code_outh.equals("200")) {

							}
						}
						handler.sendEmptyMessage(RESET_SECCEED);
					} else {
						handler.sendEmptyMessage(RESET_FAILD);
					}
				} else if ("500".equals(code) || "300".equals(code)) {
					String message = jsonObject.getString("message");
					handler.obtainMessage(_RESET_FAILED, message)
							.sendToTarget();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			String message = (String) msg.obj;
			switch (msg.what) {
			case _SENDRESET_FAILED:
				showMsg(getResources().getString(R.string.sendvalidate_fail)
						+ "，" + message, "alert", "top");
				break;
			case SENDRESET_SECCEED:
				showMsg(getResources().getString(R.string.sendvalidate_success),
						"alert", "top");
				break;
			case SENDRESET_FAILD:
				showMsg(getResources().getString(R.string.sendvalidate_fail),
						"alert", "top");
				break;
			case PROGRESSDIALOG:
				pd.dismiss();
				break;
			case _RESET_FAILED:
				pd.dismiss();
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				showMsg(getResources().getString(R.string.verify_fail) + "，"
						+ message, "alert", "top");
				break;
			case RESET_SECCEED:
				pd.dismiss();
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				showMsg(getResources().getString(R.string.verify_success),
						"alert", "top");
				setResult(Activity.RESULT_OK); // 通知上级Activity
				// 如果是新注册用户，跳转到提交昵称、头像页面
				// if(newregister){
				// Intent intent = new
				// Intent(ValidateActivity.this,RegisterHeadAndNickname.class);
				// intent.putExtra("authapi", authapi);
				// startActivity(intent);
				// finish();
				// //通知页面finish
				// }else{
				// finish();
				// }
				break;
			case RESET_FAILD:
				pd.dismiss();
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				showMsg(getResources().getString(R.string.verify_fail),
						"alert", "top");
				break;
			case _PROGRESSDIALOG:
				pd.dismiss();
				btn_val_val.setEnabled(false);
				btn_val_val.setBackgroundResource(R.drawable.res_blank_val);
				task = new TimerTask() {
					public void run() {
						Message message = new Message();
						message.what = 11;
						handler1.sendMessage(message);
					}
				};
				timer.schedule(task, 0, 1000);
				break;
			}
		}
	};

	final Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 11:
				time--;
				btn_val_val.setText(time
						+ getResources().getString(R.string.regetvalidateno));
				if (time == 0) {
					task.cancel();
					task = null;
					timer.cancel();
					timer = null;
					btn_val_val.setText("");
					btn_val_val.setEnabled(true);
					btn_val_val.setBackgroundResource(R.drawable.btn_res_val);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(StValidateActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			backdialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backdialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.tips_backvalidate));
		builder.setPositiveButton(getString(R.string.dialog_confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.setNegativeButton(getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
