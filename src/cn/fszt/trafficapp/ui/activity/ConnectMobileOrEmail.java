package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 绑定邮箱/手机
 * @author AeiouKong
 *
 */
public class ConnectMobileOrEmail extends Activity implements OnClickListener{
	
	private static final int MOBILE_CONNECT_SUCCESS = 0x1;
	private static final int EMAIL_CONNECT_SUCCESS = 0x2;
	private static final int CONNECT_FAILED = 0x3;
	private static final int _CONNECT_FAILED = 0x4;
	private static final int PROGRESSDIALOG = 0x5;
	
	private ProgressDialog pd;
	
	private String pwd,new_pwd,request_url,updateinfo_url,auth_code,usertype;
	private EditText et_con_user,et_con_pwd;
	private ImageView btn_con_submit;
	private int uid;
	private String user;  //用户输入的账号，手机/邮箱
	private RelativeLayout rl_con_pwd,rl_con_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_connectmobileoremail);
		
		Intent intent = getIntent();
		usertype = intent.getStringExtra("usertype");
		
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		
		pwd = sp.getString("pwd", null);
		uid = sp.getInt("uid", 0);
		auth_code = sp.getString("auth_code", null);
		
		initView();
		
		getActionBar().setTitle(getResources().getString(R.string.connectaccount));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
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
	
	private void initView(){
		request_url = getResources().getString(R.string.passport);
//		updateinfo_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.updateuserinfo_url)+"&connected_uid=";
		et_con_user = (EditText) findViewById(R.id.et_con_user);
		btn_con_submit = (ImageView) findViewById(R.id.btn_con_submit);
		btn_con_submit.setOnClickListener(this);
		pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.binding));
        pd.setCanceledOnTouchOutside(false);
        rl_con_pwd = (RelativeLayout) findViewById(R.id.rl_con_pwd);
        rl_con_login = (RelativeLayout) findViewById(R.id.rl_con_login);
        et_con_pwd = (EditText) findViewById(R.id.et_con_pwd);
        if(pwd == null){
        	rl_con_pwd.setVisibility(View.VISIBLE);
        	rl_con_login.setBackgroundResource(R.drawable.preference_first_item);
        }
        if(usertype!=null && usertype.equals("mobile")){
			et_con_user.setHint("手机");
			et_con_user.setInputType(InputType.TYPE_CLASS_PHONE);
		}else if(usertype!=null && usertype.equals("email")){
			et_con_user.setHint("邮箱");
		}
	}

	@Override
	public void onClick(View v) {
		if(v == btn_con_submit){
			if(pwd!=null){
				user = et_con_user.getText().toString();
				if(!user.equals("")){
					String f_email = getResources().getString(R.string.email_matches);
					String f_mobile = getResources().getString(R.string.mobile_matches);
					if(usertype!=null && usertype.equals("mobile")){
						if(user.matches(f_mobile)){
							pd.show();
							new Thread(){
								public void run() {
									connect(user,pwd,"connect_mobile","mobile",uid+"",auth_code);
									handler.sendEmptyMessage(PROGRESSDIALOG);
								}
							}.start();
						}else{
							Toast.makeText(ConnectMobileOrEmail.this, "请输入手机", Toast.LENGTH_LONG).show();
						}
						
					}else if(usertype!=null && usertype.equals("email")){
						if(user.matches(f_email)){
							pd.show();
							new Thread(){
								public void run() {
									connect(user,pwd,"connect_email","email",uid+"",auth_code);
									handler.sendEmptyMessage(PROGRESSDIALOG);
								}
							}.start();
						}else{
							Toast.makeText(ConnectMobileOrEmail.this, "请输入邮箱", Toast.LENGTH_LONG).show();
						}
					}
				}else if(user.equals("")){
					Toast.makeText(this, "请输入账号信息", Toast.LENGTH_LONG).show();
				}
			}
			
			
			else{
				user = et_con_user.getText().toString();
				new_pwd = et_con_pwd.getText().toString();
				if(!user.equals("") && !new_pwd.equals("") && new_pwd.length()>=6 && new_pwd.length()<=12){
					String f_email = "\\p{Alpha}\\w{2,25}[@][a-z0-9]{2,}[.]\\p{Lower}{2,}";
					String f_mobile = "(13[0-9]|145|147|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|4|5|6|7|8|9])\\d{8}";
					if(usertype!=null && usertype.equals("mobile")){
						if(user.matches(f_mobile)){
							pd.show();
							new Thread(){
								public void run() {
									connect(user,new_pwd,"connect_mobile","mobile",uid+"",auth_code);
									//保留登录密码，绑定手机的时候用
									SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();
									editor.putString("pwd", new_pwd);
									editor.commit();
									handler.sendEmptyMessage(PROGRESSDIALOG);
								}
							}.start();
						}else{
							Toast.makeText(ConnectMobileOrEmail.this, "请输入手机", Toast.LENGTH_LONG).show();
						}
						
					}else if(usertype!=null && usertype.equals("email")){
						if(user.matches(f_email)){
							pd.show();
							new Thread(){
								public void run() {
									connect(user,new_pwd,"connect_email","email",uid+"",auth_code);
									//保留登录密码，绑定手机的时候用
									SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();
									editor.putString("pwd", new_pwd);
									editor.commit();
									handler.sendEmptyMessage(PROGRESSDIALOG);
								}
							}.start();
						}else{
							Toast.makeText(ConnectMobileOrEmail.this, "请输入邮箱", Toast.LENGTH_LONG).show();
						}
					}
				}else if(user.equals("")&&new_pwd.equals("")){
					Toast.makeText(this, "请输入账号信息", Toast.LENGTH_LONG).show();
				}else if(user.equals("")){
					Toast.makeText(this, "请输入账号信息", Toast.LENGTH_LONG).show();
				}else if(new_pwd.equals("")||new_pwd.length()<6||new_pwd.length()>12){
					Toast.makeText(this, "请输入6-12位密码", Toast.LENGTH_LONG).show();
				}
			}
			
			
		}
	}
	
	private void connect(String user,String pwd,String api,String usertype,String uid,String auth_code){
		JSONObject jsonObject;
		List <NameValuePair> params=new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("api",api));
	    params.add(new BasicNameValuePair("version","1.0"));
	    params.add(new BasicNameValuePair(usertype,user));
	    params.add(new BasicNameValuePair("password",pwd));
	    params.add(new BasicNameValuePair("uid",uid));
	    params.add(new BasicNameValuePair("auth_code",auth_code));
		String result = HttpUtil.PostStringFromUrl(request_url, params);
		if(result!=null){
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				if(code !=null && code.equals("200")){
				boolean connect_result = jsonObject.getBoolean("connect_result");
				if(connect_result){
					//绑定成功
					if(api.equals("connect_mobile")){
//						HttpUtil.GetStringFromUrl(updateinfo_url+uid+"&mobile="+user);
						handler.sendEmptyMessage(MOBILE_CONNECT_SUCCESS);
					}else if(api.equals("connect_email")){
//						HttpUtil.GetStringFromUrl(updateinfo_url+uid+"&email="+user);
						handler.sendEmptyMessage(EMAIL_CONNECT_SUCCESS);
					}
				}else{
					handler.sendEmptyMessage(CONNECT_FAILED);
				}
			}else if(code.equals("500")){
				String message = jsonObject.getString("message");
				handler.obtainMessage(_CONNECT_FAILED, message).sendToTarget();;
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Intent intent = getIntent();
			switch(msg.what){
			case MOBILE_CONNECT_SUCCESS:
				Toast.makeText(ConnectMobileOrEmail.this, "绑定成功", Toast.LENGTH_LONG).show();
				intent.putExtra("user", user);
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case EMAIL_CONNECT_SUCCESS:
				Toast.makeText(ConnectMobileOrEmail.this, "绑定成功", Toast.LENGTH_LONG).show();
				intent.putExtra("user", user);
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case CONNECT_FAILED:
				Toast.makeText(ConnectMobileOrEmail.this, "绑定失败", Toast.LENGTH_LONG).show();
				break;
			case _CONNECT_FAILED:
				String message = (String) msg.obj;
				Toast.makeText(ConnectMobileOrEmail.this, "绑定失败，"+message, Toast.LENGTH_LONG).show();
				break;
			case PROGRESSDIALOG:
				pd.dismiss();
				break;
			
			}
			
		}
	};
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
