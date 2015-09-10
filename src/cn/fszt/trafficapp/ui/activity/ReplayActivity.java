package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.ui.service.RadioPlayerService;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.ShareCountUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.SelectSinglePhoto;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 点播室主界面
 * 
 * @author AeiouKong
 *
 */
public class ReplayActivity extends BaseBackActivity implements OnClickListener, OnUploadProcessListener {

	private TextView tv_pgname, tv_re_begin, tv_re_end;
	private ImageView iv_re_bg, btn_radioplay, btn_radiostop;
	private ProgressBar pb_re_huanchong;
	private String playurl, hdreplaytypeid, pgname, image, imagebackground, djname;// 节目名称、头像
	private String hdreplayid, imagepath;// 当期节目主键id，当期节目背景图
	private static boolean flag; // 标记是否正在播放，flase为停止，true为正在播放
	private static boolean playflag; // 标记播放暂停的图标
	private boolean progressflag; // 标记进度条进程
	private boolean playorpause; // 标记播放线程
	private Intent intent;
	private static final int REPLAYITEM = 1;
	private static final int UPLOAD_FILE_DONE = 2;
	private static final int TO_SELECT_PHOTO = 3;
	private static final int BG_FROM_URL = 4;

	private String updatereplayimg_url, picPath, api_url;
	private UploadUtil upload;
	private ProgressDialog pd_uploadbg;
	public static SeekBar audioSeekBar = null;

	private SharedPreferences sp; // 存储是否正在播放的信息

	private String _cameraDir;
	private String imgFileName = null;
	private Bitmap bitmap;

	private SharedPreferences sharedpreference; // 用户信息

	Uri imguri = null;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private MenuItem item_re_changbg;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi;

		if (densityDpi == 240) {
			setContentView(R.layout.activity_replay_hdpi);
		} else if (densityDpi == 160) {
			setContentView(R.layout.activity_replay_mdpi);
		} else {
			setContentView(R.layout.activity_replay);
		}
		initView();

		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image);

		// 加载节目背景图
		imageLoader.displayImage(imagepath, iv_re_bg, options, null);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// checkNetworkInfo();
				autoplay();
			}
		}, 200);

		new Thread() {
			public void run() {
				setPlayTimes(hdreplayid);
			}
		}.start();

		getActionBar().setTitle(getResources().getString(R.string.replay));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getOverflowMenu();

	}

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.replayradioactivity, menu);

		item_re_changbg = menu.findItem(R.id.item_re_changbg);

		sharedpreference = getSharedPreferences("USER", Context.MODE_PRIVATE); // 用户信息
		String typename = sharedpreference.getString("typename", null);
		if (typename != null) {
			String[] typenames = typename.split(",");
			for (String s : typenames) {
				if (s.equals(pgname)) {
					item_re_changbg.setVisible(true);
				}
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_re_hudong:
			Intent intent = new Intent(this, RadioCommentListActivity.class);
			intent.putExtra("pgname", pgname);
			intent.putExtra("djname", djname);
			intent.putExtra("image", image);
			intent.putExtra("imagebackground", imagebackground);
			startActivity(intent);
			break;

		case R.id.item_re_list:
			Intent intentitem = new Intent(this, ReplayLiveListActivity.class);
			intentitem.putExtra("id", hdreplaytypeid);
			startActivityForResult(intentitem, REPLAYITEM);
			break;

		case R.id.item_re_changbg:
			Intent intent_changbg = new Intent(this, SelectSinglePhoto.class);
			startActivityForResult(intent_changbg, TO_SELECT_PHOTO);
			break;
		case R.id.item_re_share:
			ShareSDK.initSDK(this);
			OnekeyShare oks = new OnekeyShare();
			oks.setTitle(pgname);
			oks.setText("来自畅驾App分享");
			oks.setMusicUrl(playurl);
			oks.setUrl(playurl);
			oks.setTitleUrl(playurl);
			if (!image.isEmpty()) {
				oks.setImageUrl(image);
			} else {
				oks.setImageUrl(this.getResources().getString(R.string.qrcode_url));
			}
			oks.setCallback(new PlatformActionListener() {

				@Override
				public void onError(Platform platform, int arg1, Throwable arg2) {
					Toast.makeText(ReplayActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {

					final String plat = platform.toString();
					new Thread() {
						public void run() {
							// "5" 表示为 音频分享
							String uid = sharedpreference.getString("uuid", null);
							ShareCountUtil.ShareCount(api_url, uid, hdreplayid, "5", plat, "113.129151,23.024514");
						}
					}.start();
				}

				@Override
				public void onCancel(Platform platform, int arg1) {
					Toast.makeText(ReplayActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
				}
			});
			// 启动分享GUI
			oks.show(this);
			break;
		}
		return true;
	}

	private void autoplay() {
		playurl = intent.getStringExtra("voicepath");
		String title = intent.getStringExtra("title");
		Boolean isplaying = intent.getBooleanExtra("isplaying", false);
		// TODO 标记是否正在播放
		if (isplaying) {
			flag = true; // 原先正在播放
			playflag = false; // 页面按钮显示是播放还是暂停
			tv_pgname.setText(title);
			btn_radioplay.setBackgroundResource(R.drawable.ra_pause);
			DelayThread delaythread = new DelayThread(500); // 通知UI更新
			delaythread.start();
		} else {
			flag = false; // 选择其他节目后，重新标记
			playflag = false; // 页面按钮显示是播放还是暂停
			tv_pgname.setText(title);
			playorpause();
		}
	}

	private boolean checkNetworkInfo() {

		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (info != null) {
			State mobile = info.getState();
			if (mobile != null) {
				return ("CONNECTED").equals(mobile.toString());
			}
		}
		return false;
	}

	private void ismobiledialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.networkinfo_mobile));
		builder.setPositiveButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				play();
			}
		});
		builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void initView() {
		_cameraDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ getResources().getString(R.string.phototake_dir);
		updatereplayimg_url = getString(R.string.server_url) + getString(R.string.updatereplayimg_url);
		api_url = getString(R.string.api_url);
		intent = getIntent();
		hdreplaytypeid = intent.getStringExtra("id");
		pgname = intent.getStringExtra("pgname");
		image = intent.getStringExtra("image");
		djname = intent.getStringExtra("djname");
		imagebackground = intent.getStringExtra("imagebackground");
		hdreplayid = intent.getStringExtra("hdreplayid");
		imagepath = intent.getStringExtra("imagepath");
		btn_radioplay = (ImageView) findViewById(R.id.btn_radioplay);
		btn_radioplay.setOnClickListener(this);
		btn_radiostop = (ImageView) findViewById(R.id.btn_radiostop);
		btn_radiostop.setOnClickListener(this);
		tv_pgname = (TextView) findViewById(R.id.tv_pgname);
		tv_re_begin = (TextView) findViewById(R.id.tv_re_begin);
		tv_re_end = (TextView) findViewById(R.id.tv_re_end);
		iv_re_bg = (ImageView) findViewById(R.id.iv_re_bg);

		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);

		pd_uploadbg = new ProgressDialog(this);
		pd_uploadbg.setMessage(getResources().getString(R.string.upload));
		pd_uploadbg.setCanceledOnTouchOutside(false);

		audioSeekBar = (SeekBar) findViewById(R.id.pb_radiocurrent);
		pb_re_huanchong = (ProgressBar) findViewById(R.id.pb_re_huanchong);

		audioSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

		sp = getSharedPreferences("REPLAY", Context.MODE_PRIVATE); // 标记正在播放的节目
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REPLAYITEM && resultCode == Activity.RESULT_OK) {
			Intent intent = new Intent();
			intent.setClass(ReplayActivity.this, ReplayPlayerService.class);
			stopService(intent);// 停止Service
			playurl = data.getStringExtra("voicepath");
			String title = data.getStringExtra("title");
			imagepath = data.getStringExtra("imagepath");
			imageLoader.displayImage(imagepath, iv_re_bg, options, null);
			Editor editor = sp.edit();
			editor.putInt("childtag", data.getIntExtra("childtag", -1));
			editor.commit();

			tv_pgname.setText(title);
			flag = false; // 选择其他节目后，重新标记
			playflag = false; // 选择其他节目后，重新标记
			playorpause();
		}
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {

			picPath = data.getStringExtra(SelectSinglePhoto.KEY_PHOTO_PATH);
			File temp = new File(picPath);
			// 裁剪图片
			imguri = Uri.fromFile(temp);
			startPhotoZoom(imguri);
		}
		if (requestCode == 2 && resultCode == -1) {
			if (data != null) {
				pd_uploadbg.show();
				Map<String, String> map = new HashMap<String, String>();

				setPicToView(imguri);
				map.put("hdreplayid", hdreplayid);
				List<String> list = new ArrayList<String>();
				list.add(imgFileName);
				upload.uploadFile(list, "img", updatereplayimg_url, map);

			}
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
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 675);
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
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
			iv_re_bg.setImageBitmap(bitmap);

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
				pd_uploadbg.dismiss();
				showMsg(msg.obj + "", "info", "bottom");
				Bitmap bm = PhotoUtil.createThumbFromFile(imgFileName, 600, 600);
				iv_re_bg.setImageBitmap(bm);
				setResult(Activity.RESULT_CANCELED); // 通知上级activity更新
				break;
			case BG_FROM_URL:
				iv_re_bg.setImageBitmap(bitmap);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (ReplayPlayerService.mMediaPlayer != null) {
				int end = ReplayPlayerService.mMediaPlayer.getDuration();
				int begin = ReplayPlayerService.mMediaPlayer.getCurrentPosition();
				audioSeekBar.setMax(end);
				audioSeekBar.setProgress(begin);
				SimpleDateFormat sdf = null;
				String str_begin = null;
				String str_end = null;
				if (ReplayPlayerService.mMediaPlayer.isPlaying()) {
					if (end < 3600000) {
						sdf = new SimpleDateFormat("mm:ss");
					} else {
						sdf = new SimpleDateFormat("HH:mm:ss");
						TimeZone timezone = TimeZone.getTimeZone("GMT+00:00");
						sdf.setTimeZone(timezone);
					}
					Date dend = new Date(end);
					str_end = sdf.format(dend);
					Date dbegin = new Date(begin);
					str_begin = sdf.format(dbegin);
					tv_re_begin.setText(str_begin);
					tv_re_end.setText(str_end);
				}
			}
		}
	};

	public void playMusic(int action) {
		Intent intent = new Intent();
		intent.putExtra("MSG", action);
		intent.putExtra("url", playurl);
		intent.setClass(ReplayActivity.this, ReplayPlayerService.class);
		startService(intent);
	}

	private void playorpause() {
		// 实现消息传递
		DelayThread delaythread = new DelayThread(500);
		delaythread.start();
		if (!flag) {
			boolean ismobile = checkNetworkInfo();
			if (ismobile) {
				ismobiledialog();
			} else {
				play();
			}
		} else {
			Log.d("TAG", "playflag:" + playflag);
			playMusic(Constant.PAUSE);
			if (!playflag) {
				btn_radioplay.setBackgroundResource(R.drawable.ra_play);
				playflag = true;
			} else {
				btn_radioplay.setBackgroundResource(R.drawable.ra_pause);
				playflag = false;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_radioplay) {
			playorpause();
		}
	}

	private void setPlayTimes(String hdreplayid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "UpdateHdreplayCountByHdreplayid"));
		params.add(new BasicNameValuePair("hdreplayid", hdreplayid));
		HttpUtil.PostStringFromUrl(api_url, params);
	}

	private void play() {
		if (playurl != null) {
			btn_radioplay.setEnabled(false);
			btn_radioplay.setVisibility(View.GONE);
			pb_re_huanchong.setVisibility(View.VISIBLE);
			new Thread() {
				@Override
				public void run() {
					if (!playorpause) {
						try {
							Thread.sleep(200);
							Intent intent_replay = new Intent();
							intent_replay.setClass(ReplayActivity.this, ReplayPlayerService.class);
							stopService(intent_replay);// 停止Service
							// 停止直播
							Intent intent = new Intent();
							intent.setClass(ReplayActivity.this, RadioPlayerService.class);
							stopService(intent);// 停止Service
							setResult(Activity.RESULT_OK); // 通知直播界面切换暂停按钮
							// 开始点播
							playMusic(Constant.PLAY);
							Thread.sleep(200);
							mHandler.sendEmptyMessage(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			flag = true;
		} else {
			showMsg(getResources().getString(R.string.no_dianbo), "info", "bottom");
		}
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 3:
				pb_re_huanchong.setVisibility(View.GONE);
				btn_radioplay.setEnabled(true);
				btn_radioplay.setVisibility(View.VISIBLE);
				btn_radioplay.setBackgroundResource(R.drawable.ra_pause);
				break;
			}
		}
	};

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (ReplayPlayerService.mMediaPlayer != null) {
				ReplayPlayerService.mMediaPlayer.seekTo(seekBar.getProgress());
			}
		}
	}

	class DelayThread extends Thread {
		int milliseconds;

		public DelayThread(int i) {
			milliseconds = i;
		}

		public void run() {
			while (!progressflag) {
				try {
					sleep(milliseconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressflag = true;
		playorpause = true;
		// Intent intent = new Intent();
		// intent.setClass(ReplayRadioActivity.this, RePlayerService.class);
		// stopService(intent);//停止Service
	}

	@Override
	public void onUploadDone(int responseCode, String result, String message) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler_upload.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize) {
	}

	@Override
	public void initUpload(int fileSize) {
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO_LONG;
		}

		appMsg = AppMsg.makeText(ReplayActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		} else if (gravity.equals("center")) {
			appMsg.setLayoutGravity(Gravity.CENTER);
		}
		appMsg.show();
	}

}
