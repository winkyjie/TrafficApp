package cn.fszt.trafficapp.ui.activity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.DataCleanManager;
import cn.fszt.trafficapp.util.StUserInfoUtil;
import cn.fszt.trafficapp.util.version.ResourceInfo;
import cn.fszt.trafficapp.util.version.VersionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends BaseBackActivity implements OnClickListener {

	private final String TAG = this.getClass().getName();

	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int REQUESTCODE_USERINFO = 5;
	TextView tv_username, tv_totalscore, tv_setting_div1, tv_setting_div2, tv_cachecount;
	ImageView iv_gender, iv_login, iv_head, iv_dj;
	RelativeLayout rl_info, rl_carinfo, rl_chkupdate, rl_map, rl_aboutus, rl_uservalidate, rl_myspace, btn_logout,
			rl_licenceplate, rl_cleancache;
	LinearLayout ll_gender, ll_welcome;
	private boolean flag;

	private ResourceInfo info;
	private String localVersion, auth_code, request_url, weiboinfo_url, qq_url, uid;
	private SharedPreferences sp;
	private Bitmap headimg;

	private ImageLoader imageLoader;
	private DisplayImageOptions options_head, options_dj;
	private ProgressBar pb_setting;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	VersionUtil version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initView();

		imageLoader = ImageLoader.getInstance();

		options_head = DisplayImageOptionsUtil.getOptions(R.drawable.default_head_sq, R.drawable.default_head_sq,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));
		options_dj = DisplayImageOptionsUtil.getOptions(R.drawable.ic_transparent, R.drawable.ic_transparent,
				R.drawable.ic_transparent);

		if (uid == null) {
			rl_uservalidate.setVisibility(View.GONE);
			rl_myspace.setVisibility(View.GONE);
			rl_carinfo.setVisibility(View.GONE);
			rl_info.setVisibility(View.GONE);
			btn_logout.setVisibility(View.GONE);
			ll_gender.setVisibility(View.GONE);
			ll_welcome.setVisibility(View.VISIBLE);
			iv_head.setVisibility(View.GONE);
			pb_setting.setVisibility(View.GONE);
			tv_totalscore.setVisibility(View.GONE);
			tv_setting_div1.setVisibility(View.GONE);
			tv_setting_div2.setVisibility(View.GONE);
			rl_licenceplate.setVisibility(View.GONE);
		} else {
			String headimgurl = sp.getString("headimg", null);
			String isdjpath = sp.getString("isdjpath", null);
			if (headimgurl != null) {
				imageLoader.displayImage(headimgurl, iv_head, options_head, null);
			}
			if (isdjpath != null) {
				imageLoader.displayImage(isdjpath, iv_dj, options_dj, null);
			}
			new InitUserThread().start();
		}

		try {
			tv_cachecount.setText(DataCleanManager.getCacheSize(this.getExternalCacheDir()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getActionBar().setTitle(getResources().getString(R.string.setting));
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
		version = new VersionUtil(SettingActivity.this);
		weiboinfo_url = getResources().getString(R.string.weiboinfo_url);
		qq_url = getResources().getString(R.string.qq_url);
		request_url = getResources().getString(R.string.passport);
		btn_logout = (RelativeLayout) findViewById(R.id.rl_logout);
		btn_logout.setOnClickListener(this);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_totalscore = (TextView) findViewById(R.id.tv_totalscore);
		tv_setting_div1 = (TextView) findViewById(R.id.tv_setting_div1);
		tv_setting_div2 = (TextView) findViewById(R.id.tv_setting_div2);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head.setOnClickListener(this);
		iv_dj = (ImageView) findViewById(R.id.iv_dj);
		iv_login = (ImageView) findViewById(R.id.iv_login);
		iv_gender = (ImageView) findViewById(R.id.iv_gender);
		iv_login.setOnClickListener(this);
		rl_info = (RelativeLayout) findViewById(R.id.rl_info);
		rl_info.setOnClickListener(this);
		ll_gender = (LinearLayout) findViewById(R.id.ll_gender);
		ll_welcome = (LinearLayout) findViewById(R.id.ll_welcome);
		rl_carinfo = (RelativeLayout) findViewById(R.id.rl_carinfo);
		rl_carinfo.setOnClickListener(this);
		rl_chkupdate = (RelativeLayout) findViewById(R.id.rl_chkupdate);
		rl_chkupdate.setOnClickListener(this);
		rl_map = (RelativeLayout) findViewById(R.id.rl_map);
		rl_map.setOnClickListener(this);
		rl_aboutus = (RelativeLayout) findViewById(R.id.rl_aboutus);
		rl_aboutus.setOnClickListener(this);
		rl_uservalidate = (RelativeLayout) findViewById(R.id.rl_uservalidate);
		rl_uservalidate.setOnClickListener(this);
		rl_myspace = (RelativeLayout) findViewById(R.id.rl_myspace);
		rl_myspace.setOnClickListener(this);
		rl_licenceplate = (RelativeLayout) findViewById(R.id.rl_licenceplate);
		rl_licenceplate.setOnClickListener(this);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		tv_username.setText(sp.getString("nickname", null));
		tv_totalscore.setText("积分:" + sp.getString("totalscore", "0"));
		tv_cachecount = (TextView) findViewById(R.id.tv_cachecount);
		rl_cleancache = (RelativeLayout) findViewById(R.id.rl_cleancache);
		rl_cleancache.setOnClickListener(this);
		// TODO
		String sex = sp.getString("sex", null);
		if (sex == null) {
			iv_gender.setVisibility(View.GONE);
		} else if (sex.equals("男")) {
			iv_gender.setBackgroundResource(R.drawable.set_male);
		} else if (sex.equals("女")) {
			iv_gender.setBackgroundResource(R.drawable.set_female);
		}

		uid = sp.getString("uuid", null);
		pb_setting = (ProgressBar) findViewById(R.id.pb_setting);
	}

	@Override
	public void onClick(View v) {
		// if (v == rl_uservalidate) {
		// Intent intent = new Intent(this, StUserValidateActivity.class);
		// intent.putExtra("entrance", "setting");
		// startActivityForResult(intent, REQUESTCODE_USERINFO);
		// }
		// if (v == rl_myspace) {
		// // 我的空间
		// Intent intent = new Intent(this, StMySpaceActivity.class);
		// startActivity(intent);
		// }

		if (v == btn_logout) {
			Intent intent = new Intent(this, StLogoutActivity.class);
			startActivityForResult(intent, Constant.LOGOUT_REQUESTCODE);
		}
		if (v == iv_head) {
			Intent intent_userinfo = new Intent(this, StUserInfoActivity.class);
			startActivityForResult(intent_userinfo, REQUESTCODE_USERINFO);
		}
		if (v == rl_info) {
			if (uid != null) {
				// 返回后再拿一次资料，更新头像
				Intent intent_userinfo = new Intent(this, StUserInfoActivity.class);
				startActivityForResult(intent_userinfo, REQUESTCODE_USERINFO);
			} else {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
			}
		}
		if (v == iv_login) {
			Intent intent = new Intent(this, StLoginActivity.class);
			startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
		}
		if (v == rl_carinfo) {
			Intent intent_carinfo = new Intent(this, StCarInfoActivity.class);
			startActivityForResult(intent_carinfo, REQUESTCODE_USERINFO);
		}
		if (v == rl_chkupdate) {
			try {
				localVersion = version.getVersionName();
				CheckVersionTask cv = new CheckVersionTask();
				new Thread(cv).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// if (v == rl_map) {
		// Intent intent_mapinfo = new Intent(this, StMapInfoActivity.class);
		// startActivity(intent_mapinfo);
		// }
		if (v == rl_aboutus) {
			Intent intent_aboutus = new Intent(this, StAboutActivity.class);
			startActivity(intent_aboutus);
		}
		if (v == rl_licenceplate) {
			Intent intent_licenceplate = new Intent(this, SpLicencePlateActivity.class);
			startActivity(intent_licenceplate);
		}

		if (v == rl_cleancache) {
			tv_cachecount.setText("已清空缓存");
//			try {
//				tv_cachecount.setText(DataCleanManager.getCacheSize(this.getExternalCacheDir()));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			DataCleanManager.cleanExternalCache(this);
			DataCleanManager.cleanInternalCache(this);
			imageLoader.clearDiskCache();
			imageLoader.clearMemoryCache();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constant.LOGIN_REQUESTCODE && resultCode == RESULT_OK) {
			finish();
		}
		if (requestCode == Constant.LOGOUT_REQUESTCODE && resultCode == RESULT_OK) {
			finish();
		}
		if (requestCode == REQUESTCODE_USERINFO && resultCode == RESULT_OK) {
			// 如果有修改自有业务资料，再重新从业务服务器拿一次资料
			new UserThread().start();
		}
		if (requestCode == REQUESTCODE_USERINFO && resultCode == -3) {
			// 如果有修改通行证相关资料，再重新从通行证服务器拿一次资料
			// new InitUserThread().start();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 有修改过内容的再更新
	class UserThread extends Thread {
		@Override
		public void run() {
			if (!flag) {
				// 从业务服务器更新个人信息
				StUserInfoUtil.initSpInfo(SettingActivity.this, sp);
				// 更新头像
				String headimgurl = sp.getString("headimg", null);
				if (headimgurl != null) {
					mHandler.obtainMessage(1, headimgurl).sendToTarget();
				}
			}
		}
	}

	class InitUserThread extends Thread {
		@Override
		public void run() {
			super.run();
			StUserInfoUtil.initSpInfo(SettingActivity.this, sp);
		}
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// TODO
				String sex = sp.getString("sex", null);
				if (sex == null) {
					iv_gender.setVisibility(View.GONE);
				} else if (sex.equals("男")) {
					iv_gender.setBackgroundResource(R.drawable.set_male);
				} else if (sex.equals("女")) {
					iv_gender.setBackgroundResource(R.drawable.set_female);
				}
				tv_username.setText(sp.getString("nickname", null));
				imageLoader.displayImage((String) msg.obj, iv_head, options_head, null);
				break;
			case 2:
				iv_head.setImageBitmap(headimg);
				break;
			case 3:
				pb_setting.setVisibility(View.GONE);
				tv_totalscore.setText("积分：" + sp.getString("totalscore", "0"));
				break;
			}
		}
	};

	/*
	 * 从服务器获取xml解析并进行比对版本号
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				info = version.getResourceInfo();
				if (info.getVersion().equals(localVersion)) {
					Log.i(TAG, "版本号相同无需升级");
					handler.sendEmptyMessage(UPDATA_NONEED);
				} else {
					Log.i(TAG, "版本号不同 ,提示用户升级 ");
					handler.sendEmptyMessage(UPDATA_CLIENT);
				}
			} catch (Exception e) {
				// 待处理
				handler.sendEmptyMessage(GET_UNDATAINFO_ERROR);
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				appMsg = AppMsg.makeText(SettingActivity.this, "已经是最新版", style);
				appMsg.setLayoutGravity(Gravity.BOTTOM);
				appMsg.show();
				break;
			case UPDATA_CLIENT:
				version.showUpdataDialog(info);
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		flag = true;
		super.onDestroy();
	}

}
