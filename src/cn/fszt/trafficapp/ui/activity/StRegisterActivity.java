package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
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
import cn.jpush.android.api.JPushInterface;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 手机注册
 * @author AeiouKong
 *
 */

public class StRegisterActivity extends BaseBackActivity implements OnClickListener{
	
	private EditText et_reg_user,et_reg_pwd,et_reg_valcode,et_reg_nickname;
	private TextView tv_protocol;
	private CheckBox cb_agreeprotocol;
	private Button btn_reg_submit,btn_reg_val;
	private String api_url,mobile,pwd,nickname,valcode;
	private static final int QQ_LOGIN = 11;
    private static final int SINA_LOGIN = 12;
	private ProgressDialog pd;
	private HashMap<String, String> userinfo;
	private String authapi; //标记qq或者sina登录
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private SharedPreferences sp;
	
	private int time = Constant.VALIDATECODE_TIME;
	private Timer timer;
	private TimerTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register);		
		
		initView();
		Intent intent = getIntent();
		authapi = intent.getStringExtra("api");
		
		getActionBar().setTitle(getResources().getString(R.string.register));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private void initView() {
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		tv_protocol = (TextView) findViewById(R.id.tv_protocol);
		tv_protocol.setOnClickListener(this);
		cb_agreeprotocol = (CheckBox) findViewById(R.id.cb_agreeprotocol);
		pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.connecting));
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
		//新通行证注册url
        api_url = getString(R.string.api_url);
		et_reg_user = (EditText) findViewById(R.id.et_reg_user);
		et_reg_pwd = (EditText) findViewById(R.id.et_reg_pwd);
		et_reg_valcode = (EditText) findViewById(R.id.et_reg_valcode);
		et_reg_nickname = (EditText) findViewById(R.id.et_reg_nickname);
		btn_reg_submit = (Button) findViewById(R.id.btn_reg_submit);
		btn_reg_submit.setOnClickListener(this);
		btn_reg_val = (Button) findViewById(R.id.btn_reg_val);
		btn_reg_val.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btn_reg_submit){
			if(cb_agreeprotocol.isChecked()){
				mobile = et_reg_user.getText().toString().trim();
				pwd = et_reg_pwd.getText().toString().trim();
				nickname = et_reg_nickname.getText().toString().trim();
				valcode = et_reg_valcode.getText().toString().trim();
				if(!mobile.equals("") && !pwd.equals("") &&
						!nickname.equals("") && !valcode.equals("") &&
						pwd.length()>=6 && pwd.length()<=12){
					//执行注册
					String f_mobile = getString(R.string.mobile_matches);
					if(mobile.matches(f_mobile)){
						pd.show();
						new Thread(){
							public void run() {
								register("mobile", mobile, pwd, valcode, nickname);	
							}
						}.start();
					}else{
						showMsg(getString(R.string.inputmobileoremail), "alert", "top");
					}
				}else{
					showMsg(getString(R.string.input_wrong), "alert", "top");
				}
			}else{
				showMsg("请先选择同意用户注册协议", "alert", "top");
			}
		}
		
		if(v == tv_protocol){
			Intent intent = new Intent(this,StRegisterProtocolActivity.class);
			startActivity(intent);
		}
		
		//获取验证码
		if(v == btn_reg_val){
			// 获取验证码
			time = Constant.VALIDATECODE_TIME;
			timer = new Timer(true);
			final String mobile = et_reg_user.getText().toString();
			if(mobile!=null&&!("").equals(mobile.trim())){
				pd.show();
				new Thread(){
					public void run() {
						UserUtil.sendsms("register",mobile,handler,api_url);
					}
				}.start();
			}else{
				showMsg(getString(R.string.inputmobileoremail), "alert", "top");
			}
			
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			pd.dismiss();
			String message = msg.obj+"";
			switch (msg.what){
			//TODO
			case Constant.SENDSMS_SUCCEED:
				showMsg(message, "alert", "top");
				btn_reg_val.setEnabled(false);
				btn_reg_val.setBackgroundResource(R.drawable.res_blank_val);
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
			case Constant.REGISTER_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.REGISTER_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.REGISTER_SUCCEED:
//				showMsg(message, "alert", "top");
				finish();
				break;
			case Constant.VALIDATECODE_UI:
				time--;
				btn_reg_val.setText(time
						+ getResources().getString(R.string.regetvalidateno));
				if (time == 0) {
					task.cancel();
					task = null;
					timer.cancel();
					timer = null;
					btn_reg_val.setText("");
					btn_reg_val.setEnabled(true);
					btn_reg_val.setBackgroundResource(R.drawable.btn_res_val);
				}
				break;
			}
		}
	};
	
	//新通行证注册
	private void register(String method,String openid,String pwd,String smsauthcode,String nickname) {
		
		JSONObject jsonObject;
		List <NameValuePair> params=new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("action","register"));
	    params.add(new BasicNameValuePair("method",method));
	    //手机注册时openid为手机号，第三方注册时openid为第三方openid
	    if("mobile".equals(method)){
	    	params.add(new BasicNameValuePair("mobile", openid));
	    	//密码需要SHA1加密
	    	pwd = Base64Util.encode64(pwd); 
	    	params.add(new BasicNameValuePair("password",pwd));
	    	params.add(new BasicNameValuePair("smsauthcode",smsauthcode));
	    }else{
	    	params.add(new BasicNameValuePair("openid", openid));
//	    	params.add(new BasicNameValuePair("headimg", headimg));
	    }
	    params.add(new BasicNameValuePair("nickname",nickname));
	    params.add(new BasicNameValuePair("source","android"));
	    params.add(new BasicNameValuePair("clienttype","android"));
	    params.add(new BasicNameValuePair("clientno",getString(R.string.version_code)));
	    //TODO 极光推送的device_token
	    params.add(new BasicNameValuePair("device_token",JPushInterface.getRegistrationID(this)));
	    
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			Log.d("register_result", result);
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				String userid;
				if("200".equals(code)){
					userid = jsonObject.getString("userid");
					SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();
					editor.putString("uuid", userid);//402881ba4d4782b1014d6b29af0b0005
					editor.putString("nickname", nickname);
					editor.putString("mobile", mobile);
//					editor.putString("validate", "false"); //新注册必然是未认证
					editor.commit();
					Intent intent = getIntent();
					intent.putExtra("uid", userid);
					intent.putExtra("nickname", nickname);
					intent.putExtra("mobile", mobile);
					setResult(RESULT_OK,intent);
					handler.obtainMessage(Constant.REGISTER_SUCCEED, message).sendToTarget();
				}else{
					handler.obtainMessage(Constant.REGISTER_FAILED, message).sendToTarget();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			handler.sendEmptyMessage(Constant.REGISTER_FAILED_NULL);
		}
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		if(requestCode == VALIDATE && resultCode == RESULT_OK){
//			
//			editor.putString("validate", "true");
//			editor.commit();
//			Intent intent = getIntent();
//			intent.putExtra("validate", "true");
//			setResult(REQ_LOGIN, intent);
//			finish();
//		}
//	}
	
	private void showMsg(String msg,String styletype,String gravity){
		
		if(styletype.equals("alert")){
			style = AppMsg.STYLE_ALERT;
		}else if(styletype.equals("info")){
			style = AppMsg.STYLE_INFO;
		}
		
		appMsg = AppMsg.makeText(StRegisterActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
	}
	
	//发送短信验证码
//	private void senddialog(final String user,final String pwd){
//		AlertDialog.Builder builder = new Builder(this);
//		builder.setMessage("我们将发送验证码短信到这个号码："+user);
//		builder.setTitle("确认手机号码");
//		builder.setPositiveButton(getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				pd.show();
//				new Thread(){
//					public void run() {
//						//手机
//						register(user,pwd,"register_mobile","mobile");
//						//保留登录密码，绑定手机的时候用
//						SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
//						Editor editor = sharedPreferences.edit();
//						editor.putString("user", user);
//						editor.putString("pwd", pwd);
//						editor.commit();
//					}
//				}.start();
//			}
//		});
//		builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}
	
	public void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
    	}
    public void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
}
