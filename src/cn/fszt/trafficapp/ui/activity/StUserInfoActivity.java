package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.StUserInfoUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.SelectSinglePhoto;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.oauth.qq.AppConstants;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 用户信息界面
 * 
 * @author AeiouKong
 *
 */
public class StUserInfoActivity extends BaseBackActivity implements
		OnClickListener, OnUploadProcessListener {

	private static final int UPLOAD_FILE_DONE = 2;
	private static final int CONNECT_FAILED = 3;
	private static final int _CONNECT_FAILED = 4;
	private static final int SINA_CONNECT_SUCCESS = 5;
	private static final int QQ_CONNECT_SUCCESS = 6;
	private static final int PROGRESSDIALOG = 7;

	private RelativeLayout rl_uphead, rl_changepwd, rl_user_mobile;
	private ImageView iv_head;
	private String picPath, uid;
	private SharedPreferences sp;
	private String updateheadimg_url;
	private EditText et_user_nickname, et_user_hobby, et_user_job,
			et_user_income, et_user_intro, et_user_email;
	private EditText et_user_recname, et_user_recaddress;
	private TextView et_user_birthday, et_user_sex, et_user_mobile, et_user_qq,
			et_user_blog;
	private boolean flag_input;
	private UploadUtil upload;
	private String headimg, qq;
	private String weiboinfo_url, api_url;
	private DatePickerDialog dateDialog;
	Calendar calendar = Calendar.getInstance();

	// private WeiboAuth mWeiboAuth; //微博
	// private Oauth2AccessToken mAccessToken;
	private Tencent mTencent; // qq
	private String token, expires_in, openid;
	private String q_openid, q_token, q_expires_in;
	private String blog, qname, profile_image_url;
	private ProgressDialog pd;

	private String nickname, qqopenid, mobile;

	private String _cameraDir;
	private String imgFileName = null;
	// 选择文件
	Uri imguri = null;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;
	private MenuItem item_userinfo_update;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private Button btn_user_alternickname;
	private View rl_qq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_settings);
		initView();

		imageLoader = ImageLoader.getInstance();

		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));

		initUser();

		getActionBar().setTitle(getResources().getString(R.string.userinfo));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.userinfoactivity, menu);

		item_userinfo_update = menu.findItem(R.id.item_userinfo_update);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_userinfo_update:
			if (!flag_input) {
				et_user_recname.setEnabled(true);
				et_user_recname.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_user_recname.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_recaddress.setEnabled(true);
				et_user_recaddress.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_user_recaddress.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_email.setEnabled(true);
				et_user_email
						.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_user_email.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_sex.setEnabled(true);
				et_user_sex.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_user_sex.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_birthday.setEnabled(true);
				et_user_birthday.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_user_birthday.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_hobby.setEnabled(true);
				et_user_hobby
						.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_user_hobby.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_job.setEnabled(true);
				et_user_job.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_user_job.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_income.setEnabled(true);
				et_user_income.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_user_income.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_user_intro.setEnabled(true);
				et_user_intro
						.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_user_intro.setTextColor(getResources().getColor(
						R.color.options_item_text));
				item_userinfo_update.setTitle(R.string.item_userinfo_save);
				item_userinfo_update.setIcon(R.drawable.btn_item_save);
				flag_input = true;
			} else {
				pd.show();
				// 上传资料
				new Thread() {
					public void run() {
						updateUserInfo();
					}
				}.start();
			}
			break;
		}
		return true;
	}

	// 修改基本资料
	private void updateUserInfo() {
		String sex = et_user_sex.getText().toString();
		String birthday = et_user_birthday.getText().toString();
		String hobby = et_user_hobby.getText().toString();
		String job = et_user_job.getText().toString();
		String income = et_user_income.getText().toString();
		String intro = et_user_intro.getText().toString();
		String recname = et_user_recname.getText().toString();
		String recaddress = et_user_recaddress.getText().toString();
		String email = et_user_email.getText().toString();
		if (hobby.equals("")) {
			hobby = " ";
		}
		if (job.equals("")) {
			job = " ";
		}
		if (income.equals("")) {
			income = " ";
		}
		if (intro.equals("")) {
			intro = " ";
		}
		if (recname.equals("")) {
			recname = " ";
		}
		if (recaddress.equals("")) {
			recaddress = " ";
		}
		if (email.equals("")) {
			email = " ";
		}
		JSONObject jsonObject;
		try {
			List<NameValuePair> params_user = new ArrayList<NameValuePair>();
			params_user.add(new BasicNameValuePair("action",
					"UpdateHdpersonByUid"));
			params_user.add(new BasicNameValuePair("connected_uid", uid));
			params_user.add(new BasicNameValuePair("sex", sex));
			params_user.add(new BasicNameValuePair("birthday", birthday));
			params_user.add(new BasicNameValuePair("hobby", hobby));
			params_user.add(new BasicNameValuePair("job", job));
			params_user.add(new BasicNameValuePair("income", income));
			params_user.add(new BasicNameValuePair("intro", intro));
			params_user.add(new BasicNameValuePair("recname", recname));
			params_user.add(new BasicNameValuePair("recaddress", recaddress));
			params_user.add(new BasicNameValuePair("email", email));
			String result = HttpUtil.PostStringFromUrl(api_url, params_user);
			if (result != null) {
				Log.d("result_updateuserinfo", result);
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.UPDATEUSERINFO_SUCCEED,
							message).sendToTarget();
				} else {
					handler.obtainMessage(Constant.UPDATEUSERINFO_FAILED,
							message).sendToTarget();
				}
			} else {
				handler.sendEmptyMessage(Constant.UPDATEUSERINFO_FAILED_NULL);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 修改昵称
	private void updateNickname() {

		JSONObject jsonObject;
		try {
			List<NameValuePair> params_user = new ArrayList<NameValuePair>();
			params_user.add(new BasicNameValuePair("action",
					"UpdateHdpersonByUid"));
			params_user.add(new BasicNameValuePair("connected_uid", uid));
			params_user.add(new BasicNameValuePair("nickname", nickname));
			String result = HttpUtil.PostStringFromUrl(api_url, params_user);
			if (result != null) {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.UPDATEUSERINFO_SUCCEED,
							message).sendToTarget();
				} else {
					handler.obtainMessage(Constant.UPDATEUSERINFO_FAILED,
							message).sendToTarget();
				}
			} else {
				handler.sendEmptyMessage(Constant.UPDATEUSERINFO_FAILED_NULL);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		_cameraDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ getResources().getString(R.string.phototake_dir);
		weiboinfo_url = getResources().getString(R.string.weiboinfo_url);
		updateheadimg_url = getString(R.string.server_url)
				+ getString(R.string.updateheadimg_url);
		api_url = getString(R.string.api_url);
		rl_uphead = (RelativeLayout) findViewById(R.id.rl_uphead);
		rl_uphead.setOnClickListener(this);
		rl_changepwd = (RelativeLayout) findViewById(R.id.rl_changepwd);
		rl_changepwd.setOnClickListener(this);
		rl_qq = findViewById(R.id.rl_qq);
		rl_qq.setOnClickListener(this);
		rl_user_mobile = (RelativeLayout) findViewById(R.id.rl_user_mobile);
		rl_user_mobile.setOnClickListener(this);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head.setOnClickListener(this);
		btn_user_alternickname = (Button) findViewById(R.id.btn_user_alternickname);
		btn_user_alternickname.setOnClickListener(this);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);

		et_user_recname = (EditText) findViewById(R.id.et_user_recname);
		et_user_recaddress = (EditText) findViewById(R.id.et_user_recaddress);
		et_user_nickname = (EditText) findViewById(R.id.et_user_nickname);
		et_user_nickname.setOnClickListener(this);
		et_user_sex = (TextView) findViewById(R.id.et_user_sex);
		et_user_sex.setOnClickListener(this);
		et_user_birthday = (TextView) findViewById(R.id.et_user_birthday);
		et_user_birthday.setOnClickListener(this);
		et_user_hobby = (EditText) findViewById(R.id.et_user_hobby);
		et_user_job = (EditText) findViewById(R.id.et_user_job);
		et_user_income = (EditText) findViewById(R.id.et_user_income);
		et_user_intro = (EditText) findViewById(R.id.et_user_intro);
		et_user_qq = (TextView) findViewById(R.id.et_user_qq);

		et_user_mobile = (TextView) findViewById(R.id.et_user_mobile);
		et_user_email = (EditText) findViewById(R.id.et_user_email);

		et_user_blog = (TextView) findViewById(R.id.et_user_blog);
		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);

		dateDialog = new DatePickerDialog(this, dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));

		// mWeiboAuth = new WeiboAuth(this,
		// cn.fszt.trafficapp.oauth.sina.Constants.APP_KEY,
		// cn.fszt.trafficapp.oauth.sina.Constants.REDIRECT_URL,
		// cn.fszt.trafficapp.oauth.sina.Constants.SCOPE);

		mTencent = Tencent.createInstance(AppConstants.APP_ID,
				this.getApplicationContext());

	}

	DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {
			et_user_birthday.setText(year + "-" + (month + 1) + "-"
					+ dayOfMonth);
		}
	};

	private void initUser() {
		headimg = sp.getString("headimg", null);
		nickname = sp.getString("nickname", null);
		qqopenid = sp.getString("qqopenid", null);
		String sex = sp.getString("sex", null);
		String birthday = sp.getString("birthday", null);
		if (birthday != null) {
			String[] birs = birthday.split(" ");
			birthday = birs[0];
		}
		qq = sp.getString("qq", null);
		if (qqopenid == null || "".equals(qqopenid.trim())) {
			qq = "未绑定";
		}
		String blog = sp.getString("blog", null);
		String hobby = sp.getString("hobby", null);
		mobile = sp.getString("mobile", null);
		if (mobile == null | "".equals(mobile.trim())) {
			mobile = "未绑定";
		}
		String email = sp.getString("email", null);
		String job = sp.getString("job", null);
		String income = sp.getString("income", null);
		String intro = sp.getString("intro", null);
		String recname = sp.getString("recname", null);
		String recaddress = sp.getString("recaddress", null);

		if (headimg != null) {
			imageLoader.displayImage(headimg, iv_head, options_head, null);
		}
		et_user_recname.setText(recname);
		et_user_recaddress.setText(recaddress);
		et_user_nickname.setText(nickname);
		et_user_sex.setText(sex);
		et_user_birthday.setText(birthday);
		et_user_hobby.setText(hobby);
		et_user_job.setText(job);
		et_user_income.setText(income);
		et_user_intro.setText(intro);
		et_user_qq.setText(qq);
		et_user_mobile.setText(mobile);
		et_user_email.setText(email);
		et_user_blog.setText(blog);
	}

	@Override
	public void onClick(View v) {
		if (v == rl_qq) {
			// onClickLogin();
			if (qqopenid == null || "".equals(qqopenid.trim())) {
				onClickLogin();
			} else {
				Intent intent = new Intent(this, UnBindAccountActivity.class);
				intent.putExtra("name", qq);
				startActivityForResult(intent, Constant.UNBIND_REQUESTCODE);
			}
		}
		if (v == btn_user_alternickname) {
			alterNicknameDialog();
		}
		// 修改密码
		if (v == rl_changepwd) {
			Intent intent = new Intent(this, StChangepwdActivity.class);
			startActivityForResult(intent, Constant.CHANGEPWD_REQUESTCODE);
		}
		// 变更手机号
		if (v == rl_user_mobile) {
			if ("未绑定".equals(mobile.trim())) {
				Intent intent = new Intent(this, StChangeMobileActivity.class);
				intent.putExtra("tpye", "bind");
				startActivityForResult(intent,
						Constant.CHANGEMOBILE_REQUESTCODE);
			} else {
				Intent intent = new Intent(this, StChangeMobileActivity.class);
				intent.putExtra("tpye", "change");
				startActivityForResult(intent,
						Constant.CHANGEMOBILE_REQUESTCODE);
			}
		}
		// 修改性别
		if (v == et_user_sex) {
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.gender))
					.setItems(
							new String[] {
									getResources().getString(R.string.male),
									getResources().getString(R.string.female) },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										et_user_sex.setText(getResources()
												.getString(R.string.male));
										break;
									case 1:
										et_user_sex.setText(getResources()
												.getString(R.string.female));
										break;
									}
								}
							}).show();
		}
		// 修改生日
		if (v == et_user_birthday) {
			dateDialog.show();
		}
		// // 绑定qq
		// if (v == btn_user_qq) {
		// onClickLogin();
		// }
		// // 绑定新浪微博
		// if (v == btn_user_blog) {
		// // mWeiboAuth.anthorize(new AuthListener());
		// }
		// 绑定手机号码
		// if (v == btn_user_mobile) {
		// Intent intent_connect = new Intent(UserInfoActivity.this,
		// ConnectMobileOrEmail.class);
		// intent_connect.putExtra("usertype", "mobile");
		// startActivityForResult(intent_connect, REQUESTCODE_MOBILE);
		// }
		// 放大头像
		if (v == iv_head) {
			if (headimg != null && !headimg.equals("")) {
				Intent intent = new Intent(StUserInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgurl", headimg);
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, 0);
			} else {
				Intent intent = new Intent(StUserInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgid", R.drawable.default_head);
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, 0);
			}
		}
		// 修改头像
		if (v == rl_uphead) {
			if (uid != null) {
				Intent intent_head = new Intent(this, SelectSinglePhoto.class);
				startActivityForResult(intent_head, Constant.TO_SELECT_PHOTO);
			}
		}
	}

	// 修改昵称弹出框
	private void alterNicknameDialog() {
		AlertDialog.Builder builder = new Builder(this);
		final EditText edit = new EditText(this);
		builder.setTitle("修改昵称");
		builder.setView(edit);
		edit.setText(nickname);
		edit.setSelection(nickname.length());
		builder.setPositiveButton(getString(R.string.dialog_confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String renickname = edit.getText().toString();
						if (!"".equals(renickname)) {
							dialog.dismiss();
							nickname = renickname;
							new Thread() {
								public void run() {
									updateNickname();
								}
							}.start();
						}
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

	private void saveInfo() {
		et_user_recname.setEnabled(false);
		et_user_recname.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_recname.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_recaddress.setEnabled(false);
		et_user_recaddress.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_recaddress.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_email.setEnabled(false);
		et_user_email.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_email.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_sex.setEnabled(false);
		et_user_sex.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_sex.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_birthday.setEnabled(false);
		et_user_birthday.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_birthday.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_hobby.setEnabled(false);
		et_user_hobby.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_hobby.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_job.setEnabled(false);
		et_user_job.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_job.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_income.setEnabled(false);
		et_user_income.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_income.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_user_intro.setEnabled(false);
		et_user_intro.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_user_intro.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		item_userinfo_update.setTitle(R.string.item_userinfo_update);
		item_userinfo_update.setIcon(R.drawable.btn_item_alter);
		flag_input = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK
				&& requestCode == Constant.TO_SELECT_PHOTO) {

			picPath = data.getStringExtra(SelectSinglePhoto.KEY_PHOTO_PATH);
			File temp = new File(picPath);
			// 裁剪图片
			imguri = Uri.fromFile(temp);
			startPhotoZoom(imguri);
		}

		// 截图时按取消：resultCode=0 ， 按确定：resultCode=-1
		if (requestCode == 2 && resultCode == -1) {
			if (data != null) {
				pd.show();
				Map<String, String> map = new HashMap<String, String>();
				setPicToView(imguri);
				map.put("connected_uid", uid);
				List<String> list = new ArrayList<String>();
				list.add(imgFileName);
				upload.uploadFile(list, "img", updateheadimg_url, map);
			}
		}

		// if (resultCode == Activity.RESULT_OK
		// && requestCode == REQUESTCODE_MOBILE) {
		// // 返回的时候，重新保存一份资料
		// String user = data.getStringExtra("user");
		// et_user_mobile.setText(user);
		// btn_user_mobile.setVisibility(View.GONE);
		// // btn_user_mobile_val.setVisibility(View.VISIBLE);
		// RelativeLayout.LayoutParams params = (LayoutParams) et_user_mobile
		// .getLayoutParams();
		// params.addRule(RelativeLayout.LEFT_OF, R.id.btn_user_mobile_val);
		// et_user_mobile.setLayoutParams(params);
		// setResult(-3);
		// // new UserThread().start();
		// }

		if (resultCode == Activity.RESULT_OK
				&& requestCode == Constant.CHANGEPWD_REQUESTCODE) {
			String message = data.getStringExtra("message");
			showMsg(message, "info", "bottom");
		}
		// TODO
		if (resultCode == Activity.RESULT_OK
				&& requestCode == Constant.UNBIND_REQUESTCODE) {
			String message = data.getStringExtra("message");
			et_user_qq.setText("未绑定");
			showMsg(message, "info", "bottom");
			setResult(RESULT_OK);
		}

		if (resultCode == RESULT_OK
				&& requestCode == Constant.CHANGEMOBILE_REQUESTCODE) {
			// 变更后的手机号码
			String mobile = data.getStringExtra("mobile");
			et_user_mobile.setText(mobile);
			String message = data.getStringExtra("message");
			showMsg(message, "info", "bottom");
			new Thread() {
				public void run() {
					StUserInfoUtil.initSpInfo(StUserInfoActivity.this, sp);
					handler.sendEmptyMessage(Constant.UPDATEUSERINFO);
				}
			}.start();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 450);
		intent.putExtra("outputY", 450);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Uri imguri) {

		Bitmap bitmap;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), imguri);
			iv_head.setImageBitmap(bitmap);

			File cameraDir = new File(_cameraDir);
			if (!cameraDir.exists()) {
				cameraDir.mkdirs();
			}

			imgFileName = _cameraDir + getImageFileName();
			PhotoUtil.saveBitmap2file(bitmap, imgFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUploadDone(int responseCode, String result, String message) {

		final Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;

		pd.dismiss();
		handler_upload.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize) {
	}

	@Override
	public void initUpload(int fileSize) {
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// land
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// port
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getImageFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(date) + ".jpg";
	}

	private Handler handler_upload = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPLOAD_FILE_DONE:
				if (msg.obj.equals(getString(R.string.upload_success))) {
					showMsg(getString(R.string.upload_success), "info",
							"bottom");
					Bitmap bm = PhotoUtil.createThumbFromFile(imgFileName, 450,
							450);
					iv_head.setImageBitmap(bm);
					setResult(Activity.RESULT_OK); // 通知上级activity更新
				} else {
					// TODO
					showMsg(msg.obj + "", "info", "bottom");
				}
				break;
			case 3:
				showMsg(getResources().getString(R.string.upload_success),
						"info", "bottom");
				Bitmap bm = PhotoUtil
						.createThumbFromFile(imgFileName, 450, 450);
				iv_head.setImageBitmap(bm);
				setResult(Activity.RESULT_OK); // 通知上级activity更新
				break;
			case 4:
				showMsg(getString(R.string.upload_fail), "info", "bottom");
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String message = msg.obj + "";
			switch (msg.what) {
			// case SINA_CONNECT_SUCCESS:
			// btn_user_blog.setVisibility(View.GONE);
			// et_user_blog.setText(blog);
			// showMsg(getResources().getString(R.string.connect_success),
			// "info", "bottom");
			// setResult(-3);
			// break;
			// case QQ_CONNECT_SUCCESS:
			// btn_user_qq.setVisibility(View.GONE);
			// et_user_qq.setText(qname);
			// showMsg(getResources().getString(R.string.connect_success),
			// "info", "bottom");
			// setResult(-3);
			// break;
			// case CONNECT_FAILED:
			// showMsg(getResources().getString(R.string.connect_fail),
			// "info", "bottom");
			// break;
			// case _CONNECT_FAILED:
			// showMsg(getResources().getString(R.string.connect_fail) + "，"
			// + message, "info", "bottom");
			// break;
			case PROGRESSDIALOG:
				pd.dismiss();
				break;
			case Constant.UPDATEUSERINFO_SUCCEED:
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}
				saveInfo();
				et_user_nickname.setText(nickname);
				setResult(RESULT_OK); // 通知上级Activity更新用户信息
				showMsg(message, "info", "bottom");
				break;
			case Constant.UPDATEUSERINFO_FAILED:
				showMsg(message, "info", "bottom");
				saveInfo();
				break;
			case Constant.UPDATEUSERINFO_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "info", "bottom");
				break;
			case Constant.UPDATEUSERINFO:
				initUser();
				break;
			case Constant.BIND_FAILED:
				showMsg(message, "alert", "top");
				break;
			case Constant.BIND_FAILED_NULL:
				showMsg(getString(R.string.returndata_null), "alert", "top");
				break;
			case Constant.BIND_SUCCEED:
				et_user_qq.setText(qname);
				showMsg(message, "info", "bottom");
				setResult(RESULT_OK);
				// finish();
				break;
			}
		}
	};

	// 新浪微博
	// class AuthListener implements WeiboAuthListener {
	// @Override
	// public void onComplete(Bundle values) {
	//
	// token = values.getString("access_token");
	// expires_in = values.getString("expires_in");
	// openid = values.getString("uid");
	// mAccessToken = Oauth2AccessToken.parseAccessToken(values);
	// if (mAccessToken.isSessionValid()) {
	// AccessTokenKeeper.writeAccessToken(UserInfoActivity.this, mAccessToken);
	// pd.show();
	// new Thread(){
	// public void run() {
	// JSONObject jsonObject;
	// try {
	// String result_info =
	// HttpUtil.GetStringFromUrl(weiboinfo_url+"?uid="+openid+"&access_token="+token);
	// if(result_info!=null){
	// jsonObject = new JSONObject(result_info);
	// blog = jsonObject.getString("name");
	// connect("connect_sina",openid,token,expires_in);
	// handler.sendEmptyMessage(PROGRESSDIALOG);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }.start();
	// }
	// }
	// @Override
	// public void onCancel() {
	// showMsg(getResources().getString(R.string.canceloauth),"info","bottom");
	// }
	// @Override
	// public void onWeiboException(WeiboException e) {
	// }
	// }

	// qq授权
	private void onClickLogin() {
		IUiListener listener = new BaseUiListener();
		mTencent.login(this, "all", listener);
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(JSONObject response) {
			pd.show();
			try {
				q_openid = response.getString("openid");
				q_expires_in = response.getString("expires_in");
				q_token = response.getString("access_token");
				updateQqInfo();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
			showMsg(getResources().getString(R.string.canceloauth), "info",
					"bottom");
		}
	}

	// qq获取昵称
	private void updateQqInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IRequestListener requestListener = new IRequestListener() {
				@Override
				public void onUnknowException(Exception e, Object state) {
				}

				@Override
				public void onSocketTimeoutException(SocketTimeoutException e,
						Object state) {
				}

				@Override
				public void onNetworkUnavailableException(
						NetworkUnavailableException e, Object state) {
				}

				@Override
				public void onMalformedURLException(MalformedURLException e,
						Object state) {
				}

				@Override
				public void onJSONException(JSONException e, Object state) {
				}

				@Override
				public void onIOException(IOException e, Object state) {
				}

				@Override
				public void onHttpStatusException(HttpStatusException e,
						Object state) {
				}

				@Override
				public void onConnectTimeoutException(
						ConnectTimeoutException e, Object state) {
				}

				@Override
				public void onComplete(final JSONObject response, Object state) {
					new Thread() {
						@Override
						public void run() {
							try {
								qname = response.getString("nickname");
								// 绑定QQ
								bind("qq", q_openid);
							} catch (JSONException e) {
							}
						}
					}.start();
				}
			};
			mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
					Constants.HTTP_GET, requestListener, null);
		}
	}

	// 绑定第三方账号 TODO
	private void bind(String opentype, String openid) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "bindopenid"));
		params.add(new BasicNameValuePair("userid", uid));
		params.add(new BasicNameValuePair("opentype", opentype));
		params.add(new BasicNameValuePair("openid", openid));

		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				String message = jsonObject.getString("message");
				if ("200".equals(code)) {
					updateQqInfo(uid, qname);
					handler.obtainMessage(Constant.BIND_SUCCEED, message)
							.sendToTarget();
				} else {
					handler.obtainMessage(Constant.BIND_FAILED, message)
							.sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.BIND_FAILED_NULL);
		}
	}

	private void updateQqInfo(String uid, String qq) {
		JSONObject jsonObject;
		try {
			List<NameValuePair> params_user = new ArrayList<NameValuePair>();
			params_user.add(new BasicNameValuePair("action",
					"UpdateHdpersonByUid"));
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

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(StUserInfoActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

}
