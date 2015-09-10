package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.HomeInfoData;
import cn.fszt.trafficapp.ui.MainActivity;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.downZip.DownLoaderTask;
import cn.fszt.trafficapp.util.downZip.ZipExtractorTask;
import cn.fszt.trafficapp.util.version.VersionUtil;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	private String gethomeinfo_url, getwelcomeimg_url, settotalinstallnum, setclienttype_url;
	private ArrayList<HomeInfoData> arr_homeinfo;
	private String imagepath, code, _webDir, device_token, timestamp, shakeweb_url, uid, sysTimeStr, temp_date;// 用来记录当天是否首次打开
	private static final int SUCCEED = 1;
	private static final int IMGPATH = 2;
	private long begin, end;
	private ImageView iv_welcome;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private SharedPreferences sp, sp_user, sp_web;
	private Editor editor, editor_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.welcome);

		MobclickAgent.updateOnlineConfig(this);
		device_token = JPushInterface.getRegistrationID(this);

		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		Date nowTime = new Date(System.currentTimeMillis());
		sysTimeStr = sdf.format(nowTime);

		initView();

		new MainThread().start();
	}

	private void initView() {

		shakeweb_url = getString(R.string.shakeweb_url);
		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.welcome, R.drawable.welcome,
				R.drawable.default_image_white);

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sp.edit();
		sp_user = getSharedPreferences("USER", Context.MODE_PRIVATE);
		temp_date = sp_user.getString("temp_date", null);
		editor_user = sp_user.edit();
		editor_user.putString("device_token", device_token);
		editor_user.commit();
		uid = sp_user.getString("uuid", null);
		iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
		gethomeinfo_url = getString(R.string.server_url) + getString(R.string.gethomeinfo_url);
		getwelcomeimg_url = getString(R.string.server_url) + getString(R.string.getwelcomeimg_url);
		settotalinstallnum = getString(R.string.server_url) + getString(R.string.settotalinstallnum);
		setclienttype_url = getString(R.string.server_url) + getString(R.string.setclienttype_url);
		arr_homeinfo = new ArrayList<HomeInfoData>();

		sp_web = getSharedPreferences("WEB", Context.MODE_PRIVATE);
		timestamp = sp_web.getString("timestamp", "");
		_webDir = getFilesDir().getPath() + getString(R.string.web_dir);

		File webDir = new File(_webDir);
		if (!webDir.exists()) {
			webDir.mkdirs();
		}
		// http://218.104.177.210:9090/shake/api/downloadShake.jc?actId=402881e44af145d8014af14b139e0001&ts=

		// System.out.println("timestamp=="+timestamp);
		String url = shakeweb_url + "&ts=" + timestamp;
		DownLoaderTask task = new DownLoaderTask(url, _webDir, this);
		task.execute();
	}

	class MainThread extends Thread {

		@Override
		public void run() {
			JSONObject jsonObject;
			try {

				// 新安装的提交接口统计装机量
				boolean first = sp.getBoolean("FIRST", true);
				if (first) {
					String req_settotalinstallnum = HttpUtil
							.GetStringFromUrl(settotalinstallnum + "&clienttype=android");
					if (req_settotalinstallnum != null) {
						jsonObject = new JSONObject(req_settotalinstallnum);
						code = jsonObject.getString("code");
						if (code != null && code.equals("200")) {
							// 修改新安装标记
							editor.putBoolean("FIRST", false);
							editor.commit();
						}
					}
				}

				if (uid != null) {
					// 提交客户端类别和版本号
					VersionUtil version = new VersionUtil(WelcomeActivity.this);
					String clientno = version.getVersionName();
					HttpUtil.GetStringFromUrl(setclienttype_url + "&connected_uid=" + uid
							+ "&clienttype=android&clientno=" + clientno + "&device_token=" + device_token);
					Log.d("TAG", "device_token:" + device_token);
				}

				String req_welcomimg;
				if (uid != null) {
					if (!sysTimeStr.equals(temp_date)) {
						editor_user.putString("temp_date", sysTimeStr);
						editor_user.commit();
						req_welcomimg = HttpUtil.GetStringFromUrl(getwelcomeimg_url + "&connected_uid=" + uid);
					} else {
						req_welcomimg = HttpUtil.GetStringFromUrl(getwelcomeimg_url + "&connected_uid=multipleLogins");
					}

				} else {
					req_welcomimg = HttpUtil.GetStringFromUrl(getwelcomeimg_url + "&connected_uid=nonlogin");
				}
				if (req_welcomimg != null) {
					jsonObject = new JSONObject(req_welcomimg);
					imagepath = jsonObject.getString("imagepath");
				}
				handler.sendEmptyMessageDelayed(IMGPATH, 1000);

				String req_gethomeinfo_url = HttpUtil.GetStringFromUrl(gethomeinfo_url);
				if (req_gethomeinfo_url != null && !req_gethomeinfo_url.equals("")) {
					Type listType = new TypeToken<ArrayList<HomeInfoData>>() {
					}.getType();
					Gson gson = new Gson();
					arr_homeinfo = gson.fromJson(req_gethomeinfo_url, listType);
				}
				// handler.sendEmptyMessage(SUCCEED);

			} catch (JSONException e) {
				e.printStackTrace();
				handler.sendEmptyMessage(SUCCEED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case IMGPATH:
				imageLoader.displayImage(imagepath, iv_welcome, options, null);
				begin = System.currentTimeMillis();
				handler.sendEmptyMessage(SUCCEED);
				break;
			case SUCCEED:
				final Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				if (arr_homeinfo != null && arr_homeinfo.size() > 0) {
					intent.putExtra("homeinfo", arr_homeinfo.toArray());
				} else {
					intent.putExtra("homeinfo", "null");
				}
				end = System.currentTimeMillis();
				long result = end - begin;

				if (result < 1500) {
					new Handler().postDelayed(new Runnable() {
						// 强制1.5秒后再进入首页
						@Override
						public void run() {
							startActivity(intent);
							finish();
							overridePendingTransition(0, R.anim.zoom_exit);
						}
					}, 1500 - result);
				} else {
					startActivity(intent);
					finish();
					overridePendingTransition(0, R.anim.zoom_exit);
				}
				break;
			}
		}
	};

	public void doZipExtractorWork() {
		ZipExtractorTask task = new ZipExtractorTask(_webDir + "web.zip", _webDir, this, true);
		task.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	/**
	 * 动画屏蔽返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
