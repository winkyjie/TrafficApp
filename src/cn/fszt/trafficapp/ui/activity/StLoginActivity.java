package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.RelativeLayout;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Base64Util;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.StUserInfoUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.oauth.qq.AppConstants;
import cn.jpush.android.api.JPushInterface;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 登录授权界面，支持924账号、新浪微博、腾讯QQ、微信
 * 
 * @author AeiouKong
 *
 */
public class StLoginActivity extends BaseBackActivity implements OnClickListener {

	// private WeiboAuth mWeiboAuth;
	//
	// private Oauth2AccessToken mAccessToken;

	private String weiboinfo_url, updateinfo_oauth_url, api_url;
	boolean flag_thread;
	private SharedPreferences sp;
	private static final int OAUTHLOGININ = 98;
	private static final int QQ_LOGIN = 11;
	private static final int SINA_LOGIN = 12;

	private static final int CONNECT_FAILED = 0x21;
	private static final int _CONNECT_FAILED = 0x22;
	private static final int SINA_CONNECT_SUCCESS = 0x23;
	private static final int QQ_CONNECT_SUCCESS = 0x24;

	private RelativeLayout authBtn, authQQ;
	private ProgressDialog pd;
	private String token, openid, expires_in;
	private String q_token, q_openid, q_expires_in;

	private EditText et_lo_user, et_lo_pwd;
	private Button btn_lo_sign, btn_resetpwd, btn_lo_submit;

	private static final String APP_ID = AppConstants.APP_ID;
	private Tencent mTencent;
	private String profile_image_url;
	private String nickname; // qq昵称

	private String authapi; // 标记sina或者qq登录

	AppMsg.Style style;
	AppMsg appMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.login));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
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
		style = AppMsg.STYLE_INFO;
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		// mAccessToken = AccessTokenKeeper.readAccessToken(this);
		// // 创建微博实例
		// mWeiboAuth = new WeiboAuth(this,
		// cn.fszt.trafficapp.oauth.sina.Constants.APP_KEY,
		// cn.fszt.trafficapp.oauth.sina.Constants.REDIRECT_URL,
		// cn.fszt.trafficapp.oauth.sina.Constants.SCOPE);
		// 创建QQ实例
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		authBtn = (RelativeLayout) findViewById(R.id.rl_lo_sina);
		authBtn.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.logining));
		pd.setCanceledOnTouchOutside(false);
		weiboinfo_url = getResources().getString(R.string.weiboinfo_url);
		updateinfo_oauth_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.updateuserinfo_url);
		api_url = getString(R.string.api_url);
		et_lo_user = (EditText) findViewById(R.id.et_lo_user);
		et_lo_pwd = (EditText) findViewById(R.id.et_lo_pwd);
		btn_lo_submit = (Button) findViewById(R.id.btn_lo_submit);
		btn_lo_submit.setOnClickListener(this);
		btn_lo_sign = (Button) findViewById(R.id.btn_lo_sign);
		btn_lo_sign.setOnClickListener(this);
		authQQ = (RelativeLayout) findViewById(R.id.rl_lo_qq);
		authQQ.setOnClickListener(this);
		btn_resetpwd = (Button) findViewById(R.id.btn_resetpwd);
		btn_resetpwd.setOnClickListener(this);
	}

	// class AuthListener implements WeiboAuthListener {
	//
	// @Override
	// public void onComplete(Bundle values) {
	//
	// token = values.getString("access_token");
	// expires_in = values.getString("expires_in");
	// openid = values.getString("uid");
	// System.out.println("token==="+token);
	// System.out.println("expires_in==="+expires_in);
	// System.out.println("sinauid==="+openid);
	// mAccessToken = Oauth2AccessToken.parseAccessToken(values);
	// if (mAccessToken.isSessionValid()) {
	// AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
	// new Thread(){
	// public void run() {
	// JSONObject jsonObject;
	// //登录前显示progressdialog
	// mHandler.sendEmptyMessage(0x4);
	// try {
	// //从新浪微博获取第三方用户头像
	// String result_info =
	// HttpUtil.GetStringFromUrl(weiboinfo_url+"?uid="+openid+"&access_token="+token);
	// if(result_info!=null){
	// jsonObject = new JSONObject(result_info);
	// profile_image_url = jsonObject.getString("profile_image_url");
	// nickname = jsonObject.getString("name");
	// oauthok("oauthok_sina",openid,token,expires_in,profile_image_url);
	// mHandler.sendEmptyMessage(0x5);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }.start();
	// }
	// }
	//
	// @Override
	// public void onCancel() {
	// showMsg(getResources().getString(R.string.canceloauth),"info","bottom");
	// }
	//
	// @Override
	// public void onWeiboException(WeiboException e) {
	// }
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO
		if (requestCode == Constant.REGISTER_REQUESTCODE && resultCode == RESULT_OK) {
			// 用来中转，新注册的用户，由昵称界面直接跳回要登录才能执行的界面，比如我报路况的提交按钮
			String nickname = data.getStringExtra("nickname");
			String uid = data.getStringExtra("uid");
			// String validate = data.getStringExtra("validate");
			Intent intent = getIntent();
			intent.putExtra("nickname", nickname);
			intent.putExtra("uid", uid);
			// intent.putExtra("validate", validate);
			setResult(RESULT_OK, intent);
			finish();
		}
		// if (requestCode == OAUTHLOGININ && resultCode == QQ_LOGIN) {
		// String username = data.getStringExtra("username");
		// connected_uid = data.getIntExtra("uid", 0);
		// auth_code = data.getStringExtra("auth_code");
		// Intent intent = getIntent();
		// intent.putExtra("username", username);
		// intent.putExtra("uid", connected_uid);
		// this.setResult(REQ_LOGIN, intent);
		// handler.sendEmptyMessage(0x4);
		// new Thread() {
		// public void run() {
		// connect("connect_qq", q_openid, q_token, q_expires_in);
		// handler.sendEmptyMessage(0x3);
		// }
		// }.start();
		//
		// }
		// if (requestCode == OAUTHLOGININ && resultCode == SINA_LOGIN) {
		//
		// String username = data.getStringExtra("username");
		// connected_uid = data.getIntExtra("uid", 0);
		// auth_code = data.getStringExtra("auth_code");
		// Intent intent = getIntent();
		// intent.putExtra("username", username);
		// intent.putExtra("uid", connected_uid);
		// this.setResult(REQ_LOGIN, intent);
		// handler.sendEmptyMessage(0x4);
		// new Thread() {
		// public void run() {
		// connect("connect_sina", openid, token, expires_in);
		// handler.sendEmptyMessage(0x3);
		// }
		// }.start();
		// }
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String message = (String) msg.obj;
			pd.dismiss();
			switch (msg.what) {
			case 0x3:
				pd.dismiss();
				finish();
				break;
			case 0x4:
				pd.show();
				break;
			case 0x5:
				pd.dismiss();
				break;
			case Constant.REGISTER_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.REGISTER_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.REGISTER_SUCCEED:
				// showMsg(message, "alert", "top");
				finish();
				break;
			// case SINA_CONNECT_SUCCESS:
			// // Toast.makeText(LoginActivity.this,
			// // getResources().getString(R.string.connect_success),
			// // Toast.LENGTH_LONG).show();
			// break;
			// case QQ_CONNECT_SUCCESS:
			// // Toast.makeText(LoginActivity.this,
			// // getResources().getString(R.string.connect_success),
			// // Toast.LENGTH_LONG).show();
			// break;
			// case CONNECT_FAILED:
			// showMsg(getResources().getString(R.string.connect_fail),
			// "alert", "top");
			// break;
			// case _CONNECT_FAILED:
			// showMsg(getResources().getString(R.string.connect_fail) + "，"
			// + (String) msg.obj, "alert", "top");
			// break;
			case Constant.LOGIN_SUCCEED:

				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(
							Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				finish();
				break;
			case Constant.LOGIN_FAILED:
				showMsg((String) msg.obj, "alert", "top");
				break;
			case Constant.LOGIN_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			}
		}
	};

	// public void showNeedRegisterDialog(String message, String yes) {
	//
	// AlertDialog.Builder builer = new Builder(LoginActivity.this);
	// builer.setTitle(getResources().getString(R.string.login_success));
	// builer.setMessage(message);
	// builer.setCancelable(false);
	//
	// builer.setPositiveButton(yes, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// Intent intent = new Intent(LoginActivity.this,
	// RegisterActivity.class);
	// intent.putExtra("api", authapi);
	// startActivityForResult(intent, OAUTHLOGININ);
	// }
	// });
	// AlertDialog dialog = builer.create();
	// dialog.show();
	// }

	@Override
	public void onClick(View v) {
		if (v == btn_resetpwd) {
			Intent intent = new Intent(this, StResetpwdActivity.class);
			startActivity(intent);
		}
		if (v == btn_lo_submit) {
			final String mobile = et_lo_user.getText().toString().trim();
			final String pwd = et_lo_pwd.getText().toString().trim();
			// 符合格式
			if (!mobile.equals("") && !pwd.equals("") && pwd.length() >= 6 && pwd.length() <= 12) {
				// 执行登录
				// String f_email =
				// getResources().getString(R.string.email_matches);
				String f_mobile = getResources().getString(R.string.mobile_matches);
				// 手机登录
				if (mobile.matches(f_mobile)) {
					pd.show();
					new Thread() {
						public void run() {
							login("password", mobile, pwd, null, sp.getString("device_token", null));
						}
					}.start();

				} else {
					showMsg(getString(R.string.inputmobileoremail), "alert", "top");
				}
			} else {
				showMsg(getString(R.string.input_wrong), "alert", "top");
			}
		}
		if (v == btn_lo_sign) {
			Intent intent = new Intent(this, StRegisterActivity.class);
			startActivityForResult(intent, Constant.REGISTER_REQUESTCODE);
		}
		// qq
		if (v == authQQ) {
			onClickLogin();
		}
		// 新浪微博
		if (v == authBtn) {
			// mWeiboAuth.anthorize(new AuthListener());
		}

	}

	/**
	 * 手机、邮箱登录
	 * 
	 * @param user
	 * @param pwd
	 * @param api
	 * @param usertype
	 */
	// private void register(String user, String pwd, String api, String
	// usertype) {
	// JSONObject jsonObject;
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("api", api));
	// params.add(new BasicNameValuePair("version", "1.0"));
	// params.add(new BasicNameValuePair(usertype, user));
	// params.add(new BasicNameValuePair("password", pwd));
	// String result = HttpUtil.PostStringFromUrl(request_url, params);
	// if (result != null) {
	// try {
	// jsonObject = new JSONObject(result);
	// String code = jsonObject.getString("code");
	// if (code.equals("200")) {
	// boolean authok = jsonObject.getBoolean("authok");
	// connected_uid = jsonObject.getInt("connected_uid");
	// String username = jsonObject.getString("username");
	// JSONObject sync_login_code = jsonObject
	// .getJSONObject("sync_login_code");
	// JSONObject first = sync_login_code.getJSONObject("1");
	// auth_code = first.getString("auth_code");
	// if (authok) {
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "USER", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// // 检查是否有上传点播背景图权限
	// String permission = HttpUtil
	// .GetStringFromUrl(replaypermission_url
	// + "&connected_uid=" + connected_uid);
	// StringBuilder sbtypename = new StringBuilder();
	// if (permission != null) {
	// jsonObject = new JSONObject(permission);
	// String permissioncode = jsonObject
	// .getString("code");
	// if (permissioncode.equals("500")) {
	// // 没有上传点播背景图权限
	// } else if (permissioncode.equals("200")) {
	// // 有上传点播背景图权限
	// StringBuilder sbhdreplaytypeid = new StringBuilder();
	// JSONArray results = jsonObject
	// .getJSONArray("result");
	// for (int i = 0; i < results.length(); i++) {
	// JSONObject permissionresult = results
	// .getJSONObject(i);
	//
	// String hdreplaytypeid = permissionresult
	// .getString("hdreplaytypeid");
	// String typename = permissionresult
	// .getString("typename");
	//
	// sbhdreplaytypeid.append(hdreplaytypeid);
	// sbhdreplaytypeid.append(",");
	// sbtypename.append(typename);
	// sbtypename.append(",");
	// // 一个主持可能多个节目，节目名称跟背景图id可能是多个
	// editor.putString("hdreplaytypeid",
	// sbhdreplaytypeid.toString());
	// }
	// }
	// }
	// // 检查是否有上传直播背景图权限
	// String permissionlive = HttpUtil
	// .GetStringFromUrl(radiopermission_url
	// + "&connected_uid=" + connected_uid);
	// if (permissionlive != null) {
	// jsonObject = new JSONObject(permissionlive);
	// String permissioncode = jsonObject
	// .getString("code");
	// if (permissioncode.equals("500")) {
	// // 没有上传直播背景图权限
	// } else if (permissioncode.equals("200")) {
	// // 有上传直播背景图权限
	// StringBuilder sbhdimagetypeid = new StringBuilder();
	// JSONArray results = jsonObject
	// .getJSONArray("result");
	// for (int i = 0; i < results.length(); i++) {
	// JSONObject permissionresult = results
	// .getJSONObject(i);
	//
	// String hdreplaytypeid = permissionresult
	// .getString("hdimagetypeid");
	// String typename = permissionresult
	// .getString("typename");
	//
	// sbhdimagetypeid.append(hdreplaytypeid);
	// sbhdimagetypeid.append(",");
	// sbtypename.append(typename);
	// sbtypename.append(",");
	//
	// editor.putString("hdimagetypeid",
	// sbhdimagetypeid.toString());
	//
	// }
	// }
	// }
	//
	// String clientno;
	// try {
	// // 提交客户端类别和版本号、更新推送device_token
	// VersionUtil version = new VersionUtil(
	// LoginActivity.this);
	// clientno = version.getVersionName();
	// HttpUtil.GetStringFromUrl(setclienttype_url
	// + "&connected_uid=" + connected_uid
	// + "&clienttype=android&clientno=" + clientno
	// + "&device_token=" +
	// UmengRegistrar.getRegistrationId(LoginActivity.this));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// // 检查是否有微博分享权限
	// // String permissionweibo =
	// //
	// HttpUtil.GetStringFromUrl(weibopermission_url+"&connected_uid="+connected_uid);
	// // if(permissionweibo!=null){
	// // jsonObject = new JSONObject(permissionweibo);
	// // String permissioncode = jsonObject.getString("code");
	// // if(permissioncode.equals("500")){
	// // //没有微博分享权限
	// // }else if(permissioncode.equals("200")){
	// // //有微博分享权限
	// // editor.putString("weibopermission", "200");
	// // }
	// // }
	//
	// editor.putString("typename", sbtypename.toString());
	// String validate = getValidate();
	// editor.putString("validate", validate);
	// editor.putInt("uid", connected_uid);
	// editor.putString("username", username);
	// editor.putString("auth_code", auth_code);
	// editor.commit();
	// // 登录成功后，在业务服务器拿一份个人资料
	// UserInfoUtil.initSpInfo(LoginActivity.this,
	// sharedPreferences, userinfo);
	//
	// Intent intent_user = getIntent();
	// intent_user.putExtra("username", username);
	// intent_user.putExtra("uid", connected_uid);
	// intent_user.putExtra("validate", validate);
	// setResult(REQ_LOGIN, intent_user);
	// finish();
	// handler.sendEmptyMessage(LOGIN_SUCCESSED);
	// } else {
	// handler.sendEmptyMessage(LOGIN_FAILED);
	// }
	// } else if (code.equals("500") || code.equals("404")) {
	// String message = jsonObject.getString("message");
	// handler.obtainMessage(_LOGIN_FAILED, message)
	// .sendToTarget();
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// TODO 新通行证登录
	private void login(String method, String mobile, String password, String smsauthcode, String device_token) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "login"));
		params.add(new BasicNameValuePair("method", method));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("device_token", device_token));
		// 手机+密码形式登录需提交密码
		if ("password".equals(method)) {
			password = Base64Util.encode64(password);
			params.add(new BasicNameValuePair("password", password));
		}
		// 手机+验证码形式需提交验证码
		else if ("smscode".equals(method)) {
			params.add(new BasicNameValuePair("smsauthcode", smsauthcode));
		}
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					String userid = jsonObject.getString("userid");
					SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();
					editor.putString("uuid", userid);
					editor.commit();
					StUserInfoUtil.initSpInfo(this, sp);
					Intent intent_user = getIntent();
					intent_user.putExtra("uid", userid);
					intent_user.putExtra("mobile", mobile);
					setResult(RESULT_OK, intent_user);
					handler.obtainMessage(Constant.LOGIN_SUCCEED, message).sendToTarget();
				} else {
					handler.obtainMessage(Constant.LOGIN_FAILED, message).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.LOGIN_FAILED_NULL);
		}
	}

	/**
	 * 新浪微博、qq授权登录
	 * 
	 * @param api
	 * @param openid
	 * @param access_token
	 * @param expires_in
	 * @param headimg
	 */
	// private void oauthok(String api, String openid, String access_token,
	// String expires_in, String headimg) {
	//
	// JSONObject jsonObject;
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("api", api));
	// params.add(new BasicNameValuePair("version", "1.0"));
	// params.add(new BasicNameValuePair("openid", openid));
	// params.add(new BasicNameValuePair("access_token", access_token));
	// params.add(new BasicNameValuePair("expires_in", expires_in));
	// String result = HttpUtil.PostStringFromUrl(request_url, params);
	// if (result != null) {
	// try {
	// jsonObject = new JSONObject(result);
	// String code = jsonObject.getString("code");
	// if (code != null && code.equals("200")) {
	// boolean authok = jsonObject.getBoolean("authok");
	// connected_uid = jsonObject.getInt("connected_uid");
	// String username = jsonObject.getString("username");
	// boolean new_registered = jsonObject
	// .getBoolean("new_registered");
	// JSONObject sync_login_code = jsonObject
	// .getJSONObject("sync_login_code");
	// JSONObject first = sync_login_code.getJSONObject("1");
	// auth_code = first.getString("auth_code");
	// if (authok) {
	// if (new_registered) {
	// // 注册
	// String req_login = LOGIN_URL + connected_uid
	// + "&nickname=" + username + "&headimg="
	// + headimg;
	// String result_login = HttpUtil
	// .GetStringFromUrl(req_login);
	// jsonObject = new JSONObject(result_login);
	// String logincode = jsonObject.getString("code");
	// if (logincode != null && logincode.equals("200")) {
	// if (api.equals("oauthok_sina")) {
	// //
	// HttpUtil.GetStringFromUrl(updateinfo_url+connected_uid+"&blog="+nickname);
	// List<NameValuePair> params_sina = new ArrayList<NameValuePair>();
	// params_sina
	// .add(new BasicNameValuePair(
	// "connected_uid",
	// connected_uid + ""));
	// params_sina.add(new BasicNameValuePair(
	// "blog", nickname));
	// HttpUtil.PostStringFromUrl(
	// updateinfo_oauth_url, params_sina);
	// } else if (api.equals("oauthok_qq")) {
	// //
	// HttpUtil.GetStringFromUrl(updateinfo_url+connected_uid+"&qq="+nickname);
	// List<NameValuePair> params_qq = new ArrayList<NameValuePair>();
	// params_qq
	// .add(new BasicNameValuePair(
	// "connected_uid",
	// connected_uid + ""));
	// params_qq.add(new BasicNameValuePair("qq",
	// nickname));
	// HttpUtil.PostStringFromUrl(
	// updateinfo_oauth_url, params_qq);
	// }
	// // 在业务服务器注册成功，主要是保存一份uid和username
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "USER", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// editor.putInt("uid", connected_uid);
	// editor.putString("username", username);
	// editor.putString("auth_code", auth_code);
	// editor.commit();
	// Intent intent_user = getIntent();
	// intent_user.putExtra("username", username);
	// intent_user.putExtra("uid", connected_uid);
	// setResult(REQ_LOGIN, intent_user);
	// finish();
	// handler.sendEmptyMessage(LOGIN_SUCCESSED);
	// }
	// } else {
	// // 登录
	// SharedPreferences sharedPreferences = getSharedPreferences(
	// "USER", Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// String permission = HttpUtil
	// .GetStringFromUrl(replaypermission_url
	// + "&connected_uid=" + connected_uid);
	// StringBuilder sbtypename = new StringBuilder();
	// if (permission != null) {
	// jsonObject = new JSONObject(permission);
	// String permissioncode = jsonObject
	// .getString("code");
	// if (permissioncode.equals("500")) {
	// // 没有上传点播背景图权限
	// } else if (permissioncode.equals("200")) {
	// // 有上传点播背景图权限
	// StringBuilder sbhdreplaytypeid = new StringBuilder();
	// JSONArray results = jsonObject
	// .getJSONArray("result");
	// for (int i = 0; i < results.length(); i++) {
	// JSONObject permissionresult = results
	// .getJSONObject(i);
	//
	// String hdreplaytypeid = permissionresult
	// .getString("hdreplaytypeid");
	// String typename = permissionresult
	// .getString("typename");
	//
	// sbhdreplaytypeid.append(hdreplaytypeid);
	// sbhdreplaytypeid.append(",");
	//
	// sbtypename.append(typename);
	// sbtypename.append(",");
	// // 一个主持可能多个节目，节目名称跟背景图id可能是多个
	// editor.putString("hdreplaytypeid",
	// sbhdreplaytypeid.toString());
	//
	// }
	// }
	// }
	// String permissionlive = HttpUtil
	// .GetStringFromUrl(radiopermission_url
	// + "&connected_uid=" + connected_uid);
	// if (permissionlive != null) {
	// jsonObject = new JSONObject(permissionlive);
	// String permissioncode = jsonObject
	// .getString("code");
	// if (permissioncode.equals("500")) {
	// // 没有上传直播背景图权限
	// } else if (permissioncode.equals("200")) {
	// // 有上传直播背景图权限
	// StringBuilder sbhdimagetypeid = new StringBuilder();
	// JSONArray results = jsonObject
	// .getJSONArray("result");
	// for (int i = 0; i < results.length(); i++) {
	// JSONObject permissionresult = results
	// .getJSONObject(i);
	//
	// String hdreplaytypeid = permissionresult
	// .getString("hdimagetypeid");
	// String typename = permissionresult
	// .getString("typename");
	//
	// sbhdimagetypeid.append(hdreplaytypeid);
	// sbhdimagetypeid.append(",");
	// sbtypename.append(typename);
	// sbtypename.append(",");
	//
	// editor.putString("hdimagetypeid",
	// sbhdimagetypeid.toString());
	//
	// }
	// }
	// }
	//
	// editor.putString("typename", sbtypename.toString());
	// String validate = getValidate();
	// editor.putString("validate", validate);
	// editor.putInt("uid", connected_uid);
	// editor.putString("username", username);
	// editor.putString("auth_code", auth_code);
	// editor.commit();
	//
	// // 登录成功后，在业务服务器拿一份个人资料
	// UserInfoUtil.initSpInfo(LoginActivity.this,
	// sharedPreferences);
	//
	// Intent intent_user = getIntent();
	// intent_user.putExtra("username", username);
	// intent_user.putExtra("uid", connected_uid);
	// intent_user.putExtra("validate", validate);
	// setResult(REQ_LOGIN, intent_user);
	// finish();
	// handler.sendEmptyMessage(LOGIN_SUCCESSED);
	// }
	// } else {
	// handler.sendEmptyMessage(LOGIN_FAILED);
	// }
	// } else if (code.equals("500")) {
	// String message = jsonObject.getString("message");
	// handler.obtainMessage(_LOGIN_FAILED, message)
	// .sendToTarget();
	// } else if (code.equals("409")) {
	// authapi = api;
	// handler.sendEmptyMessage(NEEDREGISTER);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }

	/**
	 * 绑定第三方账号
	 * 
	 * @param api
	 * @param openid
	 * @param access_token
	 * @param expires_in
	 */
	// private void connect(String api, String openid, String access_token,
	// String expires_in) {
	// JSONObject jsonObject;
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("api", api));
	// params.add(new BasicNameValuePair("version", "1.0"));
	// params.add(new BasicNameValuePair("openid", openid));
	// params.add(new BasicNameValuePair("access_token", access_token));
	// params.add(new BasicNameValuePair("expires_in", expires_in));
	// params.add(new BasicNameValuePair("uid", connected_uid + ""));
	// params.add(new BasicNameValuePair("auth_code", auth_code));
	// String result = HttpUtil.PostStringFromUrl(request_url, params);
	// if (result != null) {
	// SharedPreferences sharedPreferences = getSharedPreferences("USER",
	// Context.MODE_PRIVATE);
	// Editor editor = sharedPreferences.edit();
	// try {
	// jsonObject = new JSONObject(result);
	// String code = jsonObject.getString("code");
	// if (code != null && code.equals("200")) {
	// boolean connect_result = jsonObject
	// .getBoolean("connect_result");
	// if (connect_result) {
	// // 绑定成功
	// if (api.equals("connect_sina")) {
	// //
	// HttpUtil.GetStringFromUrl(updateinfo_url+connected_uid+"&blog="+nickname);
	// List<NameValuePair> params_sina = new ArrayList<NameValuePair>();
	// params_sina.add(new BasicNameValuePair(
	// "connected_uid", connected_uid + ""));
	// params_sina.add(new BasicNameValuePair("blog",
	// nickname));
	// HttpUtil.PostStringFromUrl(updateinfo_oauth_url,
	// params_sina);
	// editor.putString("blog", nickname); // 注册绑定成功后，更新用户资料微博名称
	// editor.commit();
	// handler.sendEmptyMessage(SINA_CONNECT_SUCCESS);
	// } else if (api.equals("connect_qq")) {
	// //
	// HttpUtil.GetStringFromUrl(updateinfo_url+connected_uid+"&qq="+nickname);
	// List<NameValuePair> params_qq = new ArrayList<NameValuePair>();
	// params_qq.add(new BasicNameValuePair(
	// "connected_uid", connected_uid + ""));
	// params_qq
	// .add(new BasicNameValuePair("qq", nickname));
	// HttpUtil.PostStringFromUrl(updateinfo_oauth_url,
	// params_qq);
	// editor.putString("qq", nickname); // 注册绑定成功后，更新用户资料QQ名称
	// editor.commit();
	// handler.sendEmptyMessage(QQ_CONNECT_SUCCESS);
	// }
	// } else {
	// handler.sendEmptyMessage(CONNECT_FAILED);
	// }
	// } else if (code.equals("500")) {
	// String message = jsonObject.getString("message");
	// handler.obtainMessage(_CONNECT_FAILED, message)
	// .sendToTarget();
	// ;
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	// qq授权
	private void onClickLogin() {
		IUiListener listener = new BaseUiListener();
		mTencent.login(this, "all", listener);
	}

	// TODO
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(JSONObject response) {
			handler.sendEmptyMessage(0x4);
			try {
				q_openid = response.getString("openid");
				q_expires_in = response.getString("expires_in");
				q_token = response.getString("access_token");
				updateUserInfo();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
			showMsg(getResources().getString(R.string.canceloauth), "info", "bottom");
		}
	}

	// qq获取头像
	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IRequestListener requestListener = new IRequestListener() {
				@Override
				public void onUnknowException(Exception e, Object state) {
				}

				@Override
				public void onSocketTimeoutException(SocketTimeoutException e, Object state) {
				}

				@Override
				public void onNetworkUnavailableException(NetworkUnavailableException e, Object state) {
				}

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {
				}

				@Override
				public void onJSONException(JSONException e, Object state) {
				}

				@Override
				public void onIOException(IOException e, Object state) {
				}

				@Override
				public void onHttpStatusException(HttpStatusException e, Object state) {
				}

				@Override
				public void onConnectTimeoutException(ConnectTimeoutException e, Object state) {
				}

				@Override
				public void onComplete(final JSONObject response, Object state) {
					new Thread() {
						@Override
						public void run() {
							try {
								nickname = response.getString("nickname").trim();
								if (nickname.length() > 10) {
									nickname=nickname.substring(0, 9);
								}
								if (response.has("figureurl")) {
									profile_image_url = response.getString("figureurl_qq_2");
								}
								// oauthok("oauthok_qq", q_openid, q_token,
								// q_expires_in, profile_image_url);
								register("qq", q_openid, null, null, nickname, profile_image_url);
								// handler.sendEmptyMessage(0x5);
							} catch (JSONException e) {
							}
						}
					}.start();
				}
			};
			mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, requestListener, null);
		} else {
		}
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(StLoginActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	// 新通行证注册
	private void register(String method, String openid, String pwd, String smsauthcode, String nickname,
			String headimg) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "register"));
		params.add(new BasicNameValuePair("method", method));
		// 手机注册时openid为手机号，第三方注册时openid为第三方openid
		if ("mobile".equals(method)) {
			params.add(new BasicNameValuePair("mobile", openid));
			// 密码需要SHA1加密
			pwd = Base64Util.encode64(pwd);
			params.add(new BasicNameValuePair("password", pwd));
			params.add(new BasicNameValuePair("smsauthcode", smsauthcode));
		} else {
			params.add(new BasicNameValuePair("openid", openid));
			params.add(new BasicNameValuePair("headimg", headimg));
		}
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("source", "android"));
		params.add(new BasicNameValuePair("clienttype", "android"));
		params.add(new BasicNameValuePair("clientno", getString(R.string.version_code)));
		// TODO 极光推送的device_token
		params.add(new BasicNameValuePair("device_token", JPushInterface.getRegistrationID(this)));

		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				String userid;
				if ("200".equals(code)) {
					userid = jsonObject.getString("userid");
					SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
					Editor editor = sharedPreferences.edit();
					editor.putString("uuid", userid);// 402881ba4d4782b1014d6b29af0b0005
					editor.putString("nickname", nickname);
					if ("qq".equals(method)) {
						// StUserInfoUtil.initSpInfo(this, sharedPreferences);
						updateQqInfo(userid, nickname);
						editor.putString("qq", nickname);
						editor.putString("headimg", headimg);
					}
					editor.commit();
					Intent intent = getIntent();
					intent.putExtra("uid", userid);
					intent.putExtra("nickname", nickname);
					setResult(RESULT_OK, intent);
					handler.obtainMessage(Constant.REGISTER_SUCCEED, message).sendToTarget();
				} else {
					handler.obtainMessage(Constant.REGISTER_FAILED, message).sendToTarget();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.REGISTER_FAILED_NULL);
		}
	}

	private void updateQqInfo(String uid, String qq) {
		JSONObject jsonObject;
		try {
			List<NameValuePair> params_user = new ArrayList<NameValuePair>();
			params_user.add(new BasicNameValuePair("action", "UpdateHdpersonByUid"));
			params_user.add(new BasicNameValuePair("connected_uid", uid));
			params_user.add(new BasicNameValuePair("qq", qq));
			String result = HttpUtil.PostStringFromUrl(api_url, params_user);
			if (result != null) {
				Log.d("result_updateuserinfo", result);
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				if ("200".equals(code)) {
				} else {
				}
			} else {
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}