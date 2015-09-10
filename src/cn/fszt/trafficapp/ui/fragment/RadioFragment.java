package cn.fszt.trafficapp.ui.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.ui.activity.RadioLiveListActivity;
import cn.fszt.trafficapp.ui.activity.ReplayListActivity;
import cn.fszt.trafficapp.ui.service.RadioPlayerService;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 直播室主界面
 * 
 * @author AeiouKong
 *
 */
public class RadioFragment extends Fragment implements OnClickListener,
		OnUploadProcessListener {

	// 直播地址
	private String radiolive_url, radiostream_url;
	private TextView tv_pgname, tv_djname, tv_current_begin, tv_current_end;
	private ProgressBar pb_radiocurrent, pb_ra_huanchong, pb_ra;
	private String playurl;
	private HashMap<String, String> info;
	private boolean flag; // 标记是否正在播放，flase为停止，true为正在播放
	private boolean playflag;
	private static final int REPLAYRADIO = 1;
	private static final int UPLOAD_FILE_DONE = 2;
	private static final int BG_FROM_URL = 4;
	private ImageView iv_ra_bg, btn_radioplay;

	private RelativeLayout rl_radio_replay, rl_radio_hudong;
	private SharedPreferences sp; // 存储点播室是否正在播放的信息
	private SharedPreferences sharedpreference; // 用户信息
	private SharedPreferences sp_bg; // 存放背景图url

	private String updateradioimg_url, picPath, getradioimg_url;

	private String _cameraDir, pg_name, hdmenuid;// 节目id
	private String imgFileName = null;
	private ProgressDialog pd_uploadbg;
	private UploadUtil upload;
	private String imagepath;// 直播节目背景图

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private MenuItem item_ra_changbg;

	Uri imguri = null;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi;
		View layout;
		if (densityDpi == 240) {
			layout = inflater.inflate(R.layout.activity_radio_hdpi, container,
					false);
		} else if (densityDpi == 160) {
			layout = inflater.inflate(R.layout.activity_radio_mdpi, container,
					false);
		} else {
			layout = inflater
					.inflate(R.layout.activity_radio, container, false);
		}

		btn_radioplay = (ImageView) layout.findViewById(R.id.btn_radioplay);
		btn_radioplay.setOnClickListener(this);
		tv_pgname = (TextView) layout.findViewById(R.id.tv_pgname);
		tv_djname = (TextView) layout.findViewById(R.id.tv_djname);
		tv_current_begin = (TextView) layout
				.findViewById(R.id.tv_current_begin);
		tv_current_end = (TextView) layout.findViewById(R.id.tv_current_end);
		pb_radiocurrent = (ProgressBar) layout
				.findViewById(R.id.pb_radiocurrent);
		pb_ra_huanchong = (ProgressBar) layout
				.findViewById(R.id.pb_ra_huanchong);
		pb_ra = (ProgressBar) layout.findViewById(R.id.pb_ra);
		iv_ra_bg = (ImageView) layout.findViewById(R.id.iv_ra_bg);
		rl_radio_replay = (RelativeLayout) layout
				.findViewById(R.id.rl_radio_replay);
		rl_radio_replay.setOnClickListener(this);
		rl_radio_hudong = (RelativeLayout) layout
				.findViewById(R.id.rl_radio_hudong);
		rl_radio_hudong.setOnClickListener(this);
		initView();
		return layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty,
				R.drawable.ic_error, R.drawable.default_image);

		sp = getActivity().getSharedPreferences("REPLAY", Context.MODE_PRIVATE);
		sp_bg = getActivity().getSharedPreferences("RADIO",
				Context.MODE_PRIVATE);
		imagepath = sp_bg.getString("imagepath", null);
		radiolive_url = getString(R.string.server_url)
				+ getString(R.string.radiolive_url);
		radiostream_url = getString(R.string.server_url)
				+ getString(R.string.radiostream_url);
		// 获取节目信息
		new RadioCurrentThread().start();
		new PlayThread().start();

		if (RadioPlayerService.mMediaPlayer != null) {
			if (RadioPlayerService.mMediaPlayer.isPlaying()) {
				flag = true;
				btn_radioplay.setBackgroundResource(R.drawable.ra_pause);
			}
		}
	}

	private void initView() {
		_cameraDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + getString(R.string.phototake_dir);
		getradioimg_url = getString(R.string.server_url)
				+ getString(R.string.getradioimg_url);
		updateradioimg_url = getString(R.string.server_url)
				+ getString(R.string.updateradioimg_url);

		if (imagepath != null && !imagepath.equals("")) {
			imageLoader.displayImage(imagepath, iv_ra_bg, options, null);
			new BackgroundThread().start();
		} else {
			new BackgroundThread().start();
		}

		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);

		pd_uploadbg = new ProgressDialog(getActivity());
		pd_uploadbg.setMessage(getResources().getString(R.string.upload));
		pd_uploadbg.setCanceledOnTouchOutside(false);

	}

	private boolean checkNetworkInfo() {

		ConnectivityManager conMan = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = conMan
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (info != null) {
			State mobile = info.getState();
			if (mobile != null) {
				return ("CONNECTED").equals(mobile.toString());
			}
		}
		return false;
	}

	/**
	 * 获取电台当前直播节目的播放流路径
	 * 
	 * @return
	 */
	private String StreamUrl() {

		JSONObject jsonObject;
		try {
			String req = HttpUtil.GetStringFromUrl(radiostream_url);
			if (req != null) {
				jsonObject = new JSONObject(req);
				String android_stream_url = jsonObject
						.getString("android_stream_url");
				return android_stream_url;
			} else {
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取电台当前直播节目的信息
	 * 
	 * @return
	 */
	public HashMap<String, String> currentinfo() {
		JSONObject jsonObject;
		try {
			String req = HttpUtil.GetStringFromUrl(radiolive_url);
			if (req != null) {
				jsonObject = new JSONObject(req);

				// 节目名称
				String pg_name = jsonObject.getString("programname");
				// 主持人名称
				String dj_name = jsonObject.getString("host");
				// 节目开始时间
				String start_time = jsonObject.getString("starttime");
				// 节目结束时间
				String end_time = jsonObject.getString("endtime");
				// 节目主持人头像
				String dj_pic_url = jsonObject.getString("imagepath");
				// 直播节目id
				String hdmenuid = jsonObject.getString("hdmenuid");

				HashMap<String, String> info = new HashMap<String, String>();
				info.put("pg_name", pg_name);
				info.put("dj_name", dj_name);
				info.put("start_time", start_time);
				info.put("end_time", end_time);
				info.put("dj_pic_url", dj_pic_url);
				info.put("hdmenuid", hdmenuid);

				return info;
			} else {
				return null;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void startPlaying() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ReplayPlayerService.class);
		getActivity().stopService(intent);// 停止点播Service

		Editor editor = sp.edit();
		editor.clear();
		editor.commit();

		playflag = false;
		if (playurl != null) {
			playMusic(Constant.PLAY);
			btn_radioplay.setEnabled(false);
			btn_radioplay.setVisibility(View.GONE);
			pb_ra_huanchong.setVisibility(View.VISIBLE);
			// 开启个线程，用来监听缓冲完毕开始播放时的状态。
			// rtsp貌似不支持api提供的onBufferUpdate监听
			new Thread() {
				public void run() {
					do {
						try {
							Thread.sleep(1000);
							if (RadioPlayerService.mMediaPlayer != null) {
								if (RadioPlayerService.mMediaPlayer.isPlaying()) {
									mHandler.sendEmptyMessage(3);
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IllegalStateException ie) {
							ie.printStackTrace();
						}
					} while (!playflag);
				}
			}.start();
		} else {
			pb_ra.setVisibility(View.GONE);
			showMsg(getResources().getString(R.string.response_fail), "info",
					"bottom");
		}
	}

	// TODO
	public void playMusic(int action) {
		Intent intent = new Intent();
		intent.putExtra("MSG", action);
		intent.putExtra("url", playurl);
		// rtsp:\/\/61.142.208.168:1935\/924\/924.sdp
		//http://yun163.ruiboyun.net/m3u8/liveshow/songerlj.m3u8?uid=101
		intent.setClass(getActivity(), RadioPlayerService.class);
		getActivity().startService(intent);
	}

	private void stopPlaying() {
		// playMusic(AppConstant.PlayerMag.PAUSE);
		if (RadioPlayerService.mMediaPlayer != null
				&& RadioPlayerService.mMediaPlayer.isPlaying()) {// 正在播放
			RadioPlayerService.mMediaPlayer.stop();
			RadioPlayerService.mMediaPlayer.reset();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_radioplay:
			autoplay();
			break;
		case R.id.rl_radio_replay:
			flag = true;
			new AppClickCount(this.getActivity(), "节目重温");
			Intent intent_dianbo = new Intent(getActivity(),
					ReplayListActivity.class);
			startActivityForResult(intent_dianbo, REPLAYRADIO);
			break;
		// TODO 点击后弹出节目时表
		case R.id.rl_radio_hudong:
			Intent intent = new Intent(getActivity(),
					RadioLiveListActivity.class);
			// intent.putExtra("pgname", pg_name);
			intent.putExtra("hdmenuid", hdmenuid);
			startActivity(intent);
			break;
		}
	}

	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent
	 data) {
	
	 if(requestCode == REPLAYRADIO && resultCode == Activity.RESULT_OK){
	 mHandler.sendEmptyMessage(2);
	 }
	 // if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO){
	 //
	 // picPath = data.getStringExtra(SelectPic.KEY_PHOTO_PATH);
	 // File temp = new File(picPath);
	 // //裁剪图片
	 // imguri = Uri.fromFile(temp);
	 // startPhotoZoom(imguri);
	 // }
	 // if(requestCode == 2 && resultCode == -1){
	 // if(data != null){
	 // pd_uploadbg.show();
	 // Map<String,String> map = new HashMap<String,String>();
	 //
	 // setPicToView(imguri);
	 // List<String> list = new ArrayList<String>();
	 // list.add(imgFileName);
	 // upload.uploadFile(list, "img", updateradioimg_url, map);
	 //
	 // }
	 // }
	 super.onActivityResult(requestCode, resultCode, data);
	 }

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	// public void startPhotoZoom(Uri uri) {
	// Intent intent = new Intent("com.android.camera.action.CROP");
	// intent.setDataAndType(uri, "image/*");
	// intent.putExtra("crop", "true");
	// intent.putExtra("aspectX", 1);
	// intent.putExtra("aspectY", 1);
	// intent.putExtra("outputX", 600);
	// intent.putExtra("outputY", 675);
	// intent.putExtra("noFaceDetection", true);
	// intent.putExtra("scale", true);
	// intent.putExtra("return-data", false);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	// intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	// startActivityForResult(intent, 2);
	// }

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	// private void setPicToView(Uri imguri) {
	//
	// Bitmap bitmap;
	// try {
	// bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
	// imguri);
	// iv_ra_bg.setImageBitmap(bitmap);
	//
	// File cameraDir = new File(_cameraDir);
	// if (!cameraDir.exists()) {
	// cameraDir.mkdirs();
	// }
	//
	// imgFileName = _cameraDir + getImageFileName();
	// PhotoUtil.saveBitmap2file(bitmap, imgFileName);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	// private String getImageFileName() {
	// Date date = new Date(System.currentTimeMillis());
	// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	// return dateFormat.format(date) + ".jpg";
	// }

	private Handler handler_upload = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPLOAD_FILE_DONE:
				// pd_uploadbg.dismiss();
				// showMsg(msg.obj+"", "info", "bottom");
				// Bitmap bm =
				// PhotoUtil.createThumbFromFile(imgFileName,600,600);
				// iv_ra_bg.setImageBitmap(bm);
				// setResult(Activity.RESULT_OK); //通知上级activity更新4
				break;
			case BG_FROM_URL:
				// iv_ra_bg.setImageBitmap(bitmap);
				String image_url = (String) msg.obj;
				imageLoader.displayImage(image_url, iv_ra_bg, options, null);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void autoplay() {

		if (!flag) {
			boolean ismobile = checkNetworkInfo();
			if (ismobile) {
				ismobiledialog();
			} else {
				new AppClickCount(this.getActivity(), "直播");
				startPlaying();
				flag = true;
			}

		} else {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(200);
						stopPlaying();
						mHandler.sendEmptyMessage(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	private void ismobiledialog() {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(getString(R.string.networkinfo_mobile));
		builder.setPositiveButton(getString(R.string.dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startPlaying();
						flag = true;
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

	private void getCurrentInfo() {

		if (info != null) {
			pb_ra.setVisibility(View.GONE);
			btn_radioplay.setVisibility(View.VISIBLE);
			String dj_name = info.get("dj_name");
			pg_name = info.get("pg_name");
			String start_time = info.get("start_time");
			String end_time = info.get("end_time");
			hdmenuid = info.get("hdmenuid");

			tv_pgname.setText(pg_name);
			tv_current_begin.setText(start_time);
			tv_current_end.setText(end_time);

			if (!"".equals(dj_name) && dj_name != null) {
				tv_djname.setText(dj_name);
			} else {
				tv_djname.setText("主持人");
			}
		}
	}

	private Object mPauseLock;
	private boolean mPauseFlag;

	/**
	 * 定时执行节目信息更新
	 * 
	 * @author AeiouKong
	 *
	 */
	class RadioCurrentThread extends Thread {

		public RadioCurrentThread() {

			mPauseLock = new Object();
			mPauseFlag = false;
		}

		// 根据mPauseFlag判断是否挂起线程
		private void pauseThread() {

			synchronized (mPauseLock) {
				if (mPauseFlag) {

					try {
						mPauseLock.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void run() {
			do {
				try {
					Log.i("RadioActivity", "in RadioCurrentThread");
					info = currentinfo();
					mHandler.sendEmptyMessage(1);
					Thread.sleep(2 * 60 * 1000); // 每隔2分钟更新一次节目信息
					pauseThread();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	// 挂起线程
	private void onPauseThread() {

		synchronized (mPauseLock) {
			mPauseFlag = true;
		}
	}

	// 唤醒线程
	private void onResumeThread() {

		synchronized (mPauseLock) {
			mPauseFlag = false;
			mPauseLock.notifyAll();
		}
	}

	class PlayThread extends Thread {
		@Override
		public void run() {
			playurl = StreamUrl();
		}
	}

	// 获取直播背景图
	class BackgroundThread extends Thread {
		Editor editor = sp_bg.edit();

		@Override
		public void run() {
			JSONObject jsonObject;
			String result = HttpUtil.GetStringFromUrl(getradioimg_url);
			if (result != null) {
				try {
					jsonObject = new JSONObject(result);
					String image_url = jsonObject.getString("imagepath");
					if (image_url != null) {
						editor.putString("imagepath", image_url);
						editor.commit();
						handler_upload.obtainMessage(BG_FROM_URL, image_url)
								.sendToTarget();

					} else {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:

				if (info != null) {
					String start_time = info.get("start_time");
					String end_time = info.get("end_time");
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
						Date nowTime = new Date(System.currentTimeMillis());
						String sysTimeStr = sdf.format(nowTime);
						Date start = sdf.parse(start_time);
						Date end = sdf.parse(end_time);
						Date sysTimeDate = sdf.parse((String) sysTimeStr);
						long progress = sysTimeDate.getTime() - start.getTime();
						long total = end.getTime() - start.getTime();
						// 退出记得停止执行
						pb_radiocurrent.setProgress((int) (pb_radiocurrent
								.getMax() * progress / total));
						getCurrentInfo();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					pb_ra.setVisibility(View.GONE);
					showMsg(getResources().getString(R.string.response_fail),
							"info", "bottom");
				}
				break;

			case 2:
				btn_radioplay.setBackgroundResource(R.drawable.ra_play);
				flag = false;
				break;

			case 3:
				playflag = true;
				pb_ra_huanchong.setVisibility(View.GONE);
				btn_radioplay.setEnabled(true);
				btn_radioplay.setVisibility(View.VISIBLE);
				btn_radioplay.setBackgroundResource(R.drawable.ra_pause);
				break;

			}
		}
	};

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
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(getActivity(), msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		} else if (gravity.equals("center")) {
			appMsg.setLayoutGravity(Gravity.CENTER);
		}
		appMsg.show();
	}

	// public void onResume() {
	// super.onResume();
	// onResumeThread();
	// MobclickAgent.onResume(getActivity());
	// }
	// public void onPause() {
	// super.onPause();
	// MobclickAgent.onPause(getActivity());
	// }
	// @Override
	// public void onStop() {
	// //跳转界面的时候，暂停更新节目信息
	// onPauseThread();
	// super.onStop();
	// }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			onPauseThread();
		} else {
			onResumeThread();
		}
	}
}
