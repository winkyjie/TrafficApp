package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.OauthUsers;
import cn.fszt.trafficapp.util.OauthUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 账号认证状态界面
 * @author AeiouKong
 *
 */
public class StUserValidateActivity extends BaseBackActivity implements OnClickListener{
	
	private EditText et_user_mobile,et_user_email;
	private SharedPreferences sp;
	private Editor editor;
	private ImageView btn_user_mobile,btn_user_email,btn_user_mobile_val,btn_user_email_val;
	private String validate,auth_code,request_url,entrance,uid;
	
	private static final int VAL_MOBILE = 12;
	private static final int VAL_EMAIL = 13;
	private static final int REQUESTCODE_MOBILE = 2;
	private static final int REQUESTCODE_EMAIL = 1;
	
	private DBManager mgr;
	private List<OauthUsers> sql_arrays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.uservalidate_settings);
		
		getActionBar().setTitle(getResources().getString(R.string.novalidate));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		initView();
		
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
		Intent intent = getIntent();
		entrance = intent.getStringExtra("entrance");
//		System.out.println("entrance====="+entrance);
		request_url = getResources().getString(R.string.passport);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		editor = sp.edit();
		String mobile = sp.getString("mobile", null);
		String email = sp.getString("email", null);
		et_user_email = (EditText) findViewById(R.id.et_user_email);
		et_user_email.setText(email);
		et_user_mobile = (EditText) findViewById(R.id.et_user_mobile);
		et_user_mobile.setText(mobile);
		btn_user_mobile = (ImageView) findViewById(R.id.btn_user_mobile);
		btn_user_mobile.setOnClickListener(this);
		btn_user_email = (ImageView) findViewById(R.id.btn_user_email);
		btn_user_email.setOnClickListener(this);
		btn_user_email_val = (ImageView) findViewById(R.id.btn_user_email_val);
		btn_user_email_val.setOnClickListener(this);
		btn_user_mobile_val = (ImageView) findViewById(R.id.btn_user_mobile_val);
		btn_user_mobile_val.setOnClickListener(this);
		mgr = new DBManager(this);
		uid = sp.getString("uuid", null);
		auth_code = sp.getString("auth_code", null);
		validate = sp.getString("validate", null);
		if(validate!=null&&validate.equals("true")){
			getActionBar().setTitle(getResources().getString(R.string.yesvalidate));
		} 
		new Thread(){
			public void run() {
				if(entrance == null){
					if(validate != null && validate.equals("false")){
						List<OauthUsers> arrays = new ArrayList<OauthUsers>();
//						arrays = OauthUtil.getDataFromNetwork(UserValidate.this, request_url, auth_code, uid);
						if(arrays!=null){
							mgr.deleteOauthusers();
							mgr.addOauthusers(arrays);
						}
					}
				}
				sql_arrays = mgr.queryOauthusers();
				
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	

	@Override
	public void onClick(View v) {
		//验证邮箱
		if(v == btn_user_email_val){
			Intent intent_val = new Intent(StUserValidateActivity.this,StValidateActivity.class);
			intent_val.putExtra("usertype", "email");
			intent_val.putExtra("email", et_user_email.getText().toString());
			startActivityForResult(intent_val,VAL_EMAIL);
		}
		//验证手机
		if(v == btn_user_mobile_val){
			Intent intent_val = new Intent(StUserValidateActivity.this,StValidateActivity.class);
			intent_val.putExtra("usertype", "mobile");
			intent_val.putExtra("mobile", et_user_mobile.getText().toString());
			startActivityForResult(intent_val,VAL_MOBILE);
		}
		//绑定手机号码
		if(v == btn_user_mobile){
			Intent intent_connect = new Intent(StUserValidateActivity.this,ConnectMobileOrEmail.class);
			intent_connect.putExtra("usertype", "mobile");
			startActivityForResult(intent_connect,REQUESTCODE_MOBILE);
		}
		//绑定邮箱
		if(v == btn_user_email){
			Intent intent_connect = new Intent(StUserValidateActivity.this,ConnectMobileOrEmail.class);
			intent_connect.putExtra("usertype", "email");
			startActivityForResult(intent_connect,REQUESTCODE_EMAIL);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//验证邮箱
		if(resultCode==Activity.RESULT_OK &&requestCode == VAL_EMAIL){
			btn_user_email_val.setVisibility(View.GONE);
			getActionBar().setTitle(getResources().getString(R.string.yesvalidate));
			editor.putString("validate", "true");
			editor.commit();
			Intent intent = getIntent();
			intent.putExtra("validate", "true");
			setResult(RESULT_OK, intent);
		}
		//验证手机
		if(resultCode==Activity.RESULT_OK &&requestCode == VAL_MOBILE){
			btn_user_mobile_val.setVisibility(View.GONE);
			getActionBar().setTitle(getResources().getString(R.string.yesvalidate));
			editor.putString("validate", "true");
			editor.commit();
			Intent intent = getIntent();
			intent.putExtra("validate", "true");
			setResult(RESULT_OK, intent);
		}
		//绑定手机
		if(resultCode==Activity.RESULT_OK &&requestCode == REQUESTCODE_MOBILE){
			//返回的时候，重新保存一份资料
			String user = data.getStringExtra("user");
			et_user_mobile.setText(user);
			btn_user_mobile.setVisibility(View.GONE);
			btn_user_mobile_val.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (LayoutParams) et_user_mobile.getLayoutParams();
			params.addRule(RelativeLayout.LEFT_OF,R.id.btn_user_mobile_val);
			et_user_mobile.setLayoutParams(params);
			setResult(RESULT_OK);
			//new UserThread().start();
		}
		//绑定邮箱
		if(resultCode==Activity.RESULT_OK &&requestCode == REQUESTCODE_EMAIL){
			String user = data.getStringExtra("user");
			et_user_email.setText(user);
			btn_user_email.setVisibility(View.GONE);
			btn_user_email_val.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (LayoutParams) et_user_email.getLayoutParams();
			params.addRule(RelativeLayout.LEFT_OF,R.id.btn_user_email_val);
			et_user_email.setLayoutParams(params);
			setResult(RESULT_OK);
			//new UserThread().start();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				if(sql_arrays!=null){
					
					for(int i=0;i<sql_arrays.size();i++){
						String openid = sql_arrays.get(i).getOpenid();
						String site = sql_arrays.get(i).getSite();
						if(site.equals("mobile")){
							et_user_mobile.setText(openid);
							editor.putString("mobile", openid);
						}
					}
					
					for(int i=0;i<sql_arrays.size();i++){
						String site = sql_arrays.get(i).getSite();
						String validate_time = sql_arrays.get(i).getValidate_time();
						//已验证手机
						if(site.equals("mobile")&&validate_time!=null){
							btn_user_mobile.setVisibility(View.GONE);
							btn_user_mobile_val.setVisibility(View.GONE);
						}
						//已验证邮箱
						else if(site.equals("email")&&validate_time!=null){
							btn_user_email.setVisibility(View.GONE);
							btn_user_email_val.setVisibility(View.GONE);
						}
						//已绑定但未验证手机
						else if(site.equals("mobile")&&validate_time==null){
							btn_user_mobile.setVisibility(View.GONE);
							btn_user_mobile_val.setVisibility(View.VISIBLE);
							RelativeLayout.LayoutParams params = (LayoutParams) et_user_mobile.getLayoutParams();
							params.addRule(RelativeLayout.LEFT_OF,R.id.btn_user_mobile_val);
							et_user_mobile.setLayoutParams(params);
						}
						//已绑定但未验证邮箱
						else if(site.equals("email")&&validate_time==null){
							btn_user_email.setVisibility(View.GONE);
							btn_user_email_val.setVisibility(View.VISIBLE);
							RelativeLayout.LayoutParams params = (LayoutParams) et_user_email.getLayoutParams();
							params.addRule(RelativeLayout.LEFT_OF,R.id.btn_user_email_val);
							et_user_email.setLayoutParams(params);
						}
					}
				}
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
