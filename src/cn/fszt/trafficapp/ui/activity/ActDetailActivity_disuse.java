package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.ActDetailAdapter;
import cn.fszt.trafficapp.domain.PinglunData;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.FileUtil;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyListview;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerByList;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * 精选活动详情
 * 
 * @author AeiouKong
 *
 */
public class ActDetailActivity_disuse extends BaseBackActivity implements OnClickListener {

	private int startindex = 1;
	private int endindex = 2;
	private static final int PINGLUN = 99;
	private static final int FULLSCREEN = 98;
	private static final int LOGININ = 97;
	private static final int VALIDATEMOBILE = 95;
	private static final int REQ_LOGIN = 10;
	private LayoutInflater inflater;
	private List<PinglunData> arrays;
	private MyListview listView;
	private ActDetailAdapter adapter;
	private TextView tv_item_title, tv_item_date, tv_item_content,
			tv_hd_votetitle, tv_hd_surveytitle;
	private ImageView btn_pl_more, btn_fullscreen, btn_mediaplay,
			btn_hd_baoming, btn_hd_tuangou;
	private String id, imgurl, weibocontent, shareurl;
	private ProgressBar pb_hd, pb_voice_huanchong;
	private ImageView iv_hd_item_img, iv_hd_item_img1, iv_hd_item_img2,
			iv_hd_item_img3, iv_hd_item_img4, iv_hd_item_img5;
	private ImageView iv_hd_item_img6, iv_hd_item_img7, iv_hd_item_img8,
			iv_hd_item_img9, iv_hd_item_img10;
	private ImageView iv_hd_item_img11, iv_hd_item_img12, iv_hd_item_img13,
			iv_hd_item_img14, iv_hd_item_img15;
	private TextView tv_hd_item_imgdesc1, tv_hd_item_imgdesc2,
			tv_hd_item_imgdesc3, tv_hd_item_imgdesc4, tv_hd_item_imgdesc5,
			tv_hd_item_newimgdesc;
	private TextView tv_hd_item_imgdesc6, tv_hd_item_imgdesc7,
			tv_hd_item_imgdesc8, tv_hd_item_imgdesc9, tv_hd_item_imgdesc10,
			tv_hd_item_videodesc, tv_hd_item_voicedesc;
	private TextView tv_hd_item_imgdesc11, tv_hd_item_imgdesc12,
			tv_hd_item_imgdesc13, tv_hd_item_imgdesc14, tv_hd_item_imgdesc15;
	private String url, hd_baoming_url, carlife_tuangou_url, weibopermission;
	private String getpl_url, delmpl_url, reportypl_url, uid;
	private TextView tv_hd_sofa;
	private LinearLayout ll_hd_pinglun, ll_hd_vote, ll_hd_survey;
	private Button btn_hd_vote, btn_hd_survey, btn_hd_action;

	private VideoView mVideoView;
	private Uri mUri = null;
	private int mPositionWhenPaused = -1;

	private MediaController mMediaController;

	private String videourl = "";
	private String voiceurl = "";

	private boolean video_flag;
	private RelativeLayout rl_videoview, rl_voiceview, rl_hd_vote,
			rl_hd_survey;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ArrayList<String> imgurls = new ArrayList<String>();
	private ProgressBar pb_videoview;
	private boolean flag; // 标记是否正在播放，flase为停止，true为正在播放
	private boolean playflag; // 标记播放暂停的图标
	private boolean progressflag; // 标记进度条进程
	private boolean playorpause; // 标记播放线程
	private TextView tv_voicetimer, tv_voicetimerbegin;
	public static SeekBar audioSeekBar = null;

	private String hdvote_title, hdvote_votetype, hdvote_startdate,
			hdvote_enddate, hdvoteid, hdsurveyid, hdvote_maxcount, action;
	private ArrayList<String> hdvoteoptions, hdvoteoptionids;

	private SharedPreferences sp;
	private String contenttype, push, newimage;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	int densityDpi, width, height;

	private MenuItem item_hd_shareweibo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;
		width = metric.widthPixels;// 宽度
		height = metric.heightPixels;// 高度
		// if(densityDpi == 240){
		// setContentView(R.layout.activity_huodongitem_hdpi);
		// }else{
		// setContentView(R.layout.activity_huodongitem);
		// }
		setContentView(R.layout.activity_huodongitem);
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		// System.out.println("id====="+id);
		contenttype = intent.getStringExtra("contenttype");
		push = intent.getStringExtra("push");
		initView();
		HuodongItemTask task = new HuodongItemTask();
		task.execute(url);
		if (contenttype != null && contenttype.equals("huodong")) {
			getActionBar().setTitle(
					getResources().getString(R.string.n_qingbao));
		} else if (contenttype != null && contenttype.equals("carlife")) {
			getActionBar().setTitle(getResources().getString(R.string.carlife));
		}
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		getOverflowMenu();
	}

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
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

		inflater.inflate(R.menu.huodongitem, menu);

//		item_hd_shareweibo = menu.findItem(R.id.item_hd_shareweibo);

		weibopermission = sp.getString("weibopermission", "500");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(ActDetailActivity_disuse.this, ActListActivity.class);
			if (push != null && "true".equals(push)) {
				if ("huodong".equals(contenttype)) {
					intent.putExtra("contenttype", "huodong");
					intent.putExtra("push", "true");
					startActivity(intent);
					finish();
				} else if ("carlife".equals(contenttype)) {
					intent.putExtra("contenttype", "carlife");
					intent.putExtra("push", "true");
					startActivity(intent);
					finish();
				}
			} else {
				finish();
			}
			break;

//		case R.id.item_hd_pingluncontent:
//			Intent intent_content = new Intent(this,
//					ActCommentListActivity.class);
//			intent_content.putExtra("id", id);
//			startActivityForResult(intent_content, PINGLUN);
//			break;
//
//		case R.id.item_hd_sentpinglun:
//			Intent intent_sent = new Intent(this, ActCommentEditActivity.class);
//			intent_sent.putExtra("id", id);
//			intent_sent.putExtra("pingluntype", "huodong");
//			startActivityForResult(intent_sent, PINGLUN);
//			break;
//
//		case R.id.item_hd_shareweibo:
//			// send(weibocontent+getResources().getString(R.string.weibocontent));
//			break;

		case R.id.item_hd_share:
			if (shareurl != null && !shareurl.equals("")) {
				showShare();
			} else {
				Toast.makeText(this, "转发失败", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}

	private void initView() {
		hdvoteoptions = new ArrayList<String>();
		hdvoteoptionids = new ArrayList<String>();
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uid", null);
		weibopermission = sp.getString("weibopermission", "500");
		url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.hditem_url) + "&hdinfoid=";
		hd_baoming_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.hd_baoming_url);
		carlife_tuangou_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.carlife_tuangou_url);
		delmpl_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.hd_delmpl_url)
				+ "&hdinfocommentid=";
		reportypl_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.reporthuodong_url)
				+ "&hdinfocommentid=";
		getpl_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.hd_getpl_url)
				+ "&hdinfoid=";
		tv_item_title = (TextView) findViewById(R.id.tv_hd_item_title);
		tv_item_date = (TextView) findViewById(R.id.tv_hd_item_date);
		tv_item_content = (TextView) findViewById(R.id.tv_hd_item_content);
		tv_hd_votetitle = (TextView) findViewById(R.id.tv_hd_votetitle);
		tv_hd_surveytitle = (TextView) findViewById(R.id.tv_hd_surveytitle);

		pb_hd = (ProgressBar) findViewById(R.id.pb_hd);
		btn_hd_vote = (Button) findViewById(R.id.btn_hd_vote);
		btn_hd_vote.setOnClickListener(this);
		btn_hd_survey = (Button) findViewById(R.id.btn_hd_survey);
		btn_hd_survey.setOnClickListener(this);
		btn_hd_action = (Button) findViewById(R.id.btn_hd_action);
		btn_hd_action.setOnClickListener(this);
		pb_videoview = (ProgressBar) findViewById(R.id.pb_videoview);
		rl_videoview = (RelativeLayout) findViewById(R.id.rl_videoview);
		rl_videoview.setOnClickListener(this);
		rl_voiceview = (RelativeLayout) findViewById(R.id.rl_voiceview);
		tv_voicetimer = (TextView) findViewById(R.id.tv_voicetimer);
		tv_voicetimerbegin = (TextView) findViewById(R.id.tv_voicetimerbegin);
		pb_voice_huanchong = (ProgressBar) findViewById(R.id.pb_voice_huanchong);

		rl_hd_vote = (RelativeLayout) findViewById(R.id.rl_hd_vote);
		rl_hd_vote.setOnClickListener(this);
		rl_hd_survey = (RelativeLayout) findViewById(R.id.rl_hd_survey);
		rl_hd_survey.setOnClickListener(this);

		iv_hd_item_img = (ImageView) findViewById(R.id.iv_hd_item_img);
		iv_hd_item_img.setMaxHeight(height);
		iv_hd_item_img.setMaxWidth(width);
		iv_hd_item_img1 = (ImageView) findViewById(R.id.iv_hd_item_img1);
		iv_hd_item_img1.setMaxHeight(height);
		iv_hd_item_img1.setMaxWidth(width);
		iv_hd_item_img2 = (ImageView) findViewById(R.id.iv_hd_item_img2);
		iv_hd_item_img2.setMaxHeight(height);
		iv_hd_item_img2.setMaxWidth(width);
		iv_hd_item_img3 = (ImageView) findViewById(R.id.iv_hd_item_img3);
		iv_hd_item_img3.setMaxHeight(height);
		iv_hd_item_img3.setMaxWidth(width);
		iv_hd_item_img4 = (ImageView) findViewById(R.id.iv_hd_item_img4);
		iv_hd_item_img4.setMaxHeight(height);
		iv_hd_item_img4.setMaxWidth(width);
		iv_hd_item_img5 = (ImageView) findViewById(R.id.iv_hd_item_img5);
		iv_hd_item_img5.setMaxHeight(height);
		iv_hd_item_img5.setMaxWidth(width);
		iv_hd_item_img6 = (ImageView) findViewById(R.id.iv_hd_item_img6);
		iv_hd_item_img6.setMaxHeight(height);
		iv_hd_item_img6.setMaxWidth(width);
		iv_hd_item_img7 = (ImageView) findViewById(R.id.iv_hd_item_img7);
		iv_hd_item_img7.setMaxHeight(height);
		iv_hd_item_img7.setMaxWidth(width);
		iv_hd_item_img8 = (ImageView) findViewById(R.id.iv_hd_item_img8);
		iv_hd_item_img8.setMaxHeight(height);
		iv_hd_item_img8.setMaxWidth(width);
		iv_hd_item_img9 = (ImageView) findViewById(R.id.iv_hd_item_img9);
		iv_hd_item_img9.setMaxHeight(height);
		iv_hd_item_img9.setMaxWidth(width);
		iv_hd_item_img10 = (ImageView) findViewById(R.id.iv_hd_item_img10);
		iv_hd_item_img10.setMaxHeight(height);
		iv_hd_item_img10.setMaxWidth(width);
		iv_hd_item_img11 = (ImageView) findViewById(R.id.iv_hd_item_img11);
		iv_hd_item_img11.setMaxHeight(height);
		iv_hd_item_img11.setMaxWidth(width);
		iv_hd_item_img12 = (ImageView) findViewById(R.id.iv_hd_item_img12);
		iv_hd_item_img12.setMaxHeight(height);
		iv_hd_item_img12.setMaxWidth(width);
		iv_hd_item_img13 = (ImageView) findViewById(R.id.iv_hd_item_img13);
		iv_hd_item_img13.setMaxHeight(height);
		iv_hd_item_img13.setMaxWidth(width);
		iv_hd_item_img14 = (ImageView) findViewById(R.id.iv_hd_item_img14);
		iv_hd_item_img14.setMaxHeight(height);
		iv_hd_item_img14.setMaxWidth(width);
		iv_hd_item_img15 = (ImageView) findViewById(R.id.iv_hd_item_img15);
		iv_hd_item_img15.setMaxHeight(height);
		iv_hd_item_img15.setMaxWidth(width);

		tv_hd_item_imgdesc1 = (TextView) findViewById(R.id.tv_hd_item_imgdesc1);
		tv_hd_item_imgdesc2 = (TextView) findViewById(R.id.tv_hd_item_imgdesc2);
		tv_hd_item_imgdesc3 = (TextView) findViewById(R.id.tv_hd_item_imgdesc3);
		tv_hd_item_imgdesc4 = (TextView) findViewById(R.id.tv_hd_item_imgdesc4);
		tv_hd_item_imgdesc5 = (TextView) findViewById(R.id.tv_hd_item_imgdesc5);
		tv_hd_item_newimgdesc = (TextView) findViewById(R.id.tv_hd_item_newimgdesc);
		tv_hd_item_imgdesc6 = (TextView) findViewById(R.id.tv_hd_item_imgdesc6);
		tv_hd_item_imgdesc7 = (TextView) findViewById(R.id.tv_hd_item_imgdesc7);
		tv_hd_item_imgdesc8 = (TextView) findViewById(R.id.tv_hd_item_imgdesc8);
		tv_hd_item_imgdesc9 = (TextView) findViewById(R.id.tv_hd_item_imgdesc9);
		tv_hd_item_imgdesc10 = (TextView) findViewById(R.id.tv_hd_item_imgdesc10);
		tv_hd_item_imgdesc11 = (TextView) findViewById(R.id.tv_hd_item_imgdesc11);
		tv_hd_item_imgdesc12 = (TextView) findViewById(R.id.tv_hd_item_imgdesc12);
		tv_hd_item_imgdesc13 = (TextView) findViewById(R.id.tv_hd_item_imgdesc13);
		tv_hd_item_imgdesc14 = (TextView) findViewById(R.id.tv_hd_item_imgdesc14);
		tv_hd_item_imgdesc15 = (TextView) findViewById(R.id.tv_hd_item_imgdesc15);

		tv_hd_item_videodesc = (TextView) findViewById(R.id.tv_hd_item_videodesc);
		tv_hd_item_voicedesc = (TextView) findViewById(R.id.tv_hd_item_voicedesc);
		audioSeekBar = (SeekBar) findViewById(R.id.pb_voice);
		audioSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

		btn_fullscreen = (ImageView) findViewById(R.id.btn_fullscreen);
		btn_fullscreen.setOnClickListener(this);
		btn_hd_baoming = (ImageView) findViewById(R.id.btn_hd_baoming);
		btn_hd_baoming.setOnClickListener(this);
		btn_hd_tuangou = (ImageView) findViewById(R.id.btn_hd_tuangou);
		btn_hd_tuangou.setOnClickListener(this);
		btn_pl_more = (ImageView) findViewById(R.id.btn_pl_more);
		btn_pl_more.setOnClickListener(this);
		btn_mediaplay = (ImageView) findViewById(R.id.btn_voice_play);
		btn_mediaplay.setOnClickListener(this);
		tv_hd_sofa = (TextView) findViewById(R.id.tv_hd_sofa);
		ll_hd_pinglun = (LinearLayout) findViewById(R.id.ll_hd_pinglun);
		ll_hd_vote = (LinearLayout) findViewById(R.id.ll_hd_vote);
		ll_hd_survey = (LinearLayout) findViewById(R.id.ll_hd_survey);
		inflater = getLayoutInflater();
		listView = (MyListview) findViewById(R.id.pd_hd_item);
		listView.setDivider(getResources().getDrawable(
				R.color.list_view_divider));
		listView.setDividerHeight(1);
		listView.setFocusable(false);
		listView.setCacheColorHint(Color.TRANSPARENT);
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				pb_videoview.setVisibility(View.GONE);
			}
		});

		mMediaController = new MediaController(this);

		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty,
				R.drawable.ic_error, R.drawable.default_image);
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle(weibocontent);
		oks.setText(weibocontent + " " + shareurl);
		oks.setUrl(shareurl);
		oks.setTitleUrl(shareurl);
		if (newimage != null && !newimage.equals("")) {
			oks.setImageUrl(newimage);
		} else {
			oks.setImageUrl("http://app.fm924.com/ztmedia/images/qrcode.png");
		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
			}

			@Override
			public void onComplete(Platform platform, int arg1,
					HashMap<String, Object> arg2) {
				Toast.makeText(ActDetailActivity_disuse.this, "分享成功", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
			}
		});
		// 启动分享GUI
		oks.show(this);

	}

	class HuodongItemTask extends
			AsyncTask<String, Integer, HashMap<String, String>> {
		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			JSONObject jsonObject;
			HashMap<String, String> map = new HashMap<String, String>();
			String result = HttpUtil.GetStringFromUrl(params[0] + id);
			// System.out.println("result==="+result);
			if (result != null && !result.equals("")) {
				try {
					jsonObject = new JSONObject(result);
					String title = jsonObject.getString("title");
					String createdate = jsonObject.getString("createdate");
					String content = jsonObject.getString("content");
					shareurl = jsonObject.getString("transmiturl");
					// System.out.println("content===="+content);
					newimage = jsonObject.getString("newimage");

					String newimagedesc = jsonObject.getString("newimagedesc");
					String imagepath1 = jsonObject.getString("imagepath1");
					String imagedesc1 = jsonObject.getString("imagedesc1");
					String imagepath2 = jsonObject.getString("imagepath2");
					String imagedesc2 = jsonObject.getString("imagedesc2");
					String imagepath3 = jsonObject.getString("imagepath3");
					String imagedesc3 = jsonObject.getString("imagedesc3");
					String imagepath4 = jsonObject.getString("imagepath4");
					String imagedesc4 = jsonObject.getString("imagedesc4");
					String imagepath5 = jsonObject.getString("imagepath5");
					String imagedesc5 = jsonObject.getString("imagedesc5");
					String imagepath6 = jsonObject.getString("imagepath6");
					String imagedesc6 = jsonObject.getString("imagedesc6");
					String imagepath7 = jsonObject.getString("imagepath7");
					String imagedesc7 = jsonObject.getString("imagedesc7");
					String imagepath8 = jsonObject.getString("imagepath8");
					String imagedesc8 = jsonObject.getString("imagedesc8");
					String imagepath9 = jsonObject.getString("imagepath9");
					String imagedesc9 = jsonObject.getString("imagedesc9");
					String imagepath10 = jsonObject.getString("imagepath10");
					String imagedesc10 = jsonObject.getString("imagedesc10");
					String imagepath11 = jsonObject.getString("imagepath11");
					String imagedesc11 = jsonObject.getString("imagedesc11");
					String imagepath12 = jsonObject.getString("imagepath12");
					String imagedesc12 = jsonObject.getString("imagedesc12");
					String imagepath13 = jsonObject.getString("imagepath13");
					String imagedesc13 = jsonObject.getString("imagedesc13");
					String imagepath14 = jsonObject.getString("imagepath14");
					String imagedesc14 = jsonObject.getString("imagedesc14");
					String imagepath15 = jsonObject.getString("imagepath15");
					String imagedesc15 = jsonObject.getString("imagedesc15");
					String voicepath = jsonObject.getString("voicepath");
					String voicedesc = jsonObject.getString("voicedesc");
					String videopath = jsonObject.getString("videopath");
					String videodesc = jsonObject.getString("videodesc");
					String hdsurveyid = jsonObject.getString("hdsurveyid");
					if (hdsurveyid != null && !hdsurveyid.equals("")) {
						String hdsurvey_title = jsonObject
								.getString("hdsurvey_title");
						map.put("hdsurvey_title", hdsurvey_title);
					}

					String hdvoteid = jsonObject.getString("hdvoteid");
					String hdactivityid = jsonObject.getString("hdactivityid");
					if (hdactivityid != null && !hdactivityid.equals("")) {
						// 可以报名
						map.put("hdactivityid", hdactivityid);
					}
					String hdgroupid = jsonObject.getString("hdgroupid");
					if (hdgroupid != null && !hdgroupid.equals("")) {
						// 可以团购
						map.put("hdgroupid", hdgroupid);
					}
					if (hdvoteid != null && !hdvoteid.equals("")) {
						String hdvote_title = jsonObject
								.getString("hdvote_title");
						String hdvote_votetype = jsonObject
								.getString("hdvote_votetype");
						String hdvote_startdate = jsonObject
								.getString("hdvote_startdate");
						String hdvote_enddate = jsonObject
								.getString("hdvote_enddate");
						String hdvote_maxcount = jsonObject
								.getString("hdvote_maxcount");
						map.put("hdvoteid", hdvoteid);
						map.put("hdvote_title", hdvote_title);
						map.put("hdvote_votetype", hdvote_votetype);
						map.put("hdvote_startdate", hdvote_startdate);
						map.put("hdvote_enddate", hdvote_enddate);
						map.put("hdvote_maxcount", hdvote_maxcount);

						JSONArray hdvoteoption = jsonObject
								.getJSONArray("hdvoteoption");
						for (int i = 0; i < hdvoteoption.length(); i++) {
							String hdvoteoption_optioncontent = hdvoteoption
									.getJSONObject(i).getString(
											"hdvoteoption_optioncontent");
							String hdvote_hdvoteoptionid = hdvoteoption
									.getJSONObject(i).getString(
											"hdvote_hdvoteoptionid");
							hdvoteoptions.add(hdvoteoption_optioncontent);
							hdvoteoptionids.add(hdvote_hdvoteoptionid);
						}
					}
					map.put("title", title);
					map.put("content", content);
					map.put("createdate", createdate);
					map.put("newimage", newimage);

					map.put("newimagedesc", newimagedesc);
					map.put("imagepath1", imagepath1);
					map.put("imagedesc1", imagedesc1);
					map.put("imagepath2", imagepath2);
					map.put("imagedesc2", imagedesc2);
					map.put("imagepath3", imagepath3);
					map.put("imagedesc3", imagedesc3);
					map.put("imagepath4", imagepath4);
					map.put("imagedesc4", imagedesc4);
					map.put("imagepath5", imagepath5);
					map.put("imagedesc5", imagedesc5);
					map.put("imagepath6", imagepath6);
					map.put("imagedesc6", imagedesc6);
					map.put("imagepath7", imagepath7);
					map.put("imagedesc7", imagedesc7);
					map.put("imagepath8", imagepath8);
					map.put("imagedesc8", imagedesc8);
					map.put("imagepath9", imagepath9);
					map.put("imagedesc9", imagedesc9);
					map.put("imagepath10", imagepath10);
					map.put("imagedesc10", imagedesc10);
					map.put("imagepath11", imagepath11);
					map.put("imagedesc11", imagedesc11);
					map.put("imagepath12", imagepath12);
					map.put("imagedesc12", imagedesc12);
					map.put("imagepath13", imagepath13);
					map.put("imagedesc13", imagedesc13);
					map.put("imagepath14", imagepath14);
					map.put("imagedesc14", imagedesc14);
					map.put("imagepath15", imagepath15);
					map.put("imagedesc15", imagedesc15);
					map.put("voicepath", voicepath);
					map.put("voicedesc", voicedesc);
					map.put("videopath", videopath);
					map.put("videodesc", videodesc);
					map.put("hdsurveyid", hdsurveyid);
					return map;
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(HashMap<String, String> map) {
			if (map != null) {
				weibocontent = map.get("title");
				tv_item_title.setText(map.get("title"));
				tv_item_date.setText(map.get("createdate"));
				String content = map.get("content");
				if (content != null) {
					content = FileUtil.ToDBC(content);
					tv_item_content.setText(content);
				}

				String newimagedesc = map.get("newimagedesc");
				if (newimagedesc != null && !newimagedesc.equals("")) {
					tv_hd_item_newimgdesc.setText(newimagedesc);
					tv_hd_item_newimgdesc.setVisibility(View.VISIBLE);
				}
				String imagedesc1 = map.get("imagedesc1");
				if (imagedesc1 != null && !imagedesc1.equals("")) {
					tv_hd_item_imgdesc1.setText(imagedesc1);
					tv_hd_item_imgdesc1.setVisibility(View.VISIBLE);
				}
				String imagedesc2 = map.get("imagedesc2");
				if (imagedesc2 != null && !imagedesc2.equals("")) {
					tv_hd_item_imgdesc2.setText(imagedesc2);
					tv_hd_item_imgdesc2.setVisibility(View.VISIBLE);
				}
				String imagedesc3 = map.get("imagedesc3");
				if (imagedesc3 != null && !imagedesc3.equals("")) {
					tv_hd_item_imgdesc3.setText(imagedesc3);
					tv_hd_item_imgdesc3.setVisibility(View.VISIBLE);
				}
				String imagedesc4 = map.get("imagedesc4");
				if (imagedesc4 != null && !imagedesc4.equals("")) {
					tv_hd_item_imgdesc4.setText(imagedesc4);
					tv_hd_item_imgdesc4.setVisibility(View.VISIBLE);
				}
				String imagedesc5 = map.get("imagedesc5");
				if (imagedesc5 != null && !imagedesc5.equals("")) {
					tv_hd_item_imgdesc5.setText(imagedesc5);
					tv_hd_item_imgdesc5.setVisibility(View.VISIBLE);
				}
				String videodesc = map.get("videodesc");
				if (videodesc != null && !videodesc.equals("")) {
					tv_hd_item_videodesc.setText(videodesc);
					tv_hd_item_videodesc.setVisibility(View.VISIBLE);
				}
				String voicedesc = map.get("voicedesc");
				if (voicedesc != null && !voicedesc.equals("")) {
					tv_hd_item_voicedesc.setText(voicedesc);
					tv_hd_item_voicedesc.setVisibility(View.VISIBLE);
				}
				String imagedesc6 = map.get("imagedesc6");
				if (imagedesc6 != null && !imagedesc6.equals("")) {
					tv_hd_item_imgdesc6.setText(imagedesc6);
					tv_hd_item_imgdesc6.setVisibility(View.VISIBLE);
				}
				String imagedesc7 = map.get("imagedesc7");
				if (imagedesc7 != null && !imagedesc7.equals("")) {
					tv_hd_item_imgdesc7.setText(imagedesc7);
					tv_hd_item_imgdesc7.setVisibility(View.VISIBLE);
				}
				String imagedesc8 = map.get("imagedesc8");
				if (imagedesc8 != null && !imagedesc8.equals("")) {
					tv_hd_item_imgdesc8.setText(imagedesc8);
					tv_hd_item_imgdesc8.setVisibility(View.VISIBLE);
				}
				String imagedesc9 = map.get("imagedesc9");
				if (imagedesc9 != null && !imagedesc9.equals("")) {
					tv_hd_item_imgdesc9.setText(imagedesc9);
					tv_hd_item_imgdesc9.setVisibility(View.VISIBLE);
				}
				String imagedesc10 = map.get("imagedesc10");
				if (imagedesc10 != null && !imagedesc10.equals("")) {
					tv_hd_item_imgdesc10.setText(imagedesc10);
					tv_hd_item_imgdesc10.setVisibility(View.VISIBLE);
				}
				String imagedesc11 = map.get("imagedesc11");
				if (imagedesc11 != null && !imagedesc11.equals("")) {
					tv_hd_item_imgdesc11.setText(imagedesc11);
					tv_hd_item_imgdesc11.setVisibility(View.VISIBLE);
				}
				String imagedesc12 = map.get("imagedesc12");
				if (imagedesc12 != null && !imagedesc12.equals("")) {
					tv_hd_item_imgdesc12.setText(imagedesc12);
					tv_hd_item_imgdesc12.setVisibility(View.VISIBLE);
				}
				String imagedesc13 = map.get("imagedesc13");
				if (imagedesc13 != null && !imagedesc13.equals("")) {
					tv_hd_item_imgdesc13.setText(imagedesc13);
					tv_hd_item_imgdesc13.setVisibility(View.VISIBLE);
				}
				String imagedesc14 = map.get("imagedesc14");
				if (imagedesc14 != null && !imagedesc14.equals("")) {
					tv_hd_item_imgdesc14.setText(imagedesc14);
					tv_hd_item_imgdesc14.setVisibility(View.VISIBLE);
				}
				String imagedesc15 = map.get("imagedesc15");
				if (imagedesc15 != null && !imagedesc15.equals("")) {
					tv_hd_item_imgdesc15.setText(imagedesc15);
					tv_hd_item_imgdesc15.setVisibility(View.VISIBLE);
				}

				String hdactivityid = map.get("hdactivityid");
				if (hdactivityid != null && !hdactivityid.equals("")) {
					btn_hd_baoming.setVisibility(View.VISIBLE);
				}

				String hdgroupid = map.get("hdgroupid");
				if (hdgroupid != null && !hdgroupid.equals("")) {
					btn_hd_tuangou.setVisibility(View.VISIBLE);
				}

				videourl = map.get("videopath");
				if (videourl != null && !videourl.equals("")) {
					mUri = Uri.parse(videourl);
					rl_videoview.setVisibility(View.VISIBLE);
				}

				voiceurl = map.get("voicepath");
				if (voiceurl != null && !voiceurl.equals("")) {
					rl_voiceview.setVisibility(View.VISIBLE);
				}

				imgurl = map.get("newimage");
				String imageUrl = imgurl;
				if (imageUrl != null && !imageUrl.equals("")) {
					imgurls.add(imgurl);
					imageLoader.displayImage(imageUrl, iv_hd_item_img, options,
							null);
					iv_hd_item_img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 0);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				} else {
					iv_hd_item_img.setVisibility(View.GONE);
				}

				final String img1 = map.get("imagepath1");
				if (img1 != null && !img1.equals("")) {
					imgurls.add(img1);
					imageLoader.displayImage(img1, iv_hd_item_img1, options,
							null);
					iv_hd_item_img1.setVisibility(View.VISIBLE);
					iv_hd_item_img1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 1);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img2 = map.get("imagepath2");
				if (img2 != null && !img2.equals("")) {
					imgurls.add(img2);
					imageLoader.displayImage(img2, iv_hd_item_img2, options,
							null);
					iv_hd_item_img2.setVisibility(View.VISIBLE);
					iv_hd_item_img2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 2);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img3 = map.get("imagepath3");
				if (img3 != null && !img3.equals("")) {
					imgurls.add(img3);
					imageLoader.displayImage(img3, iv_hd_item_img3, options,
							null);
					iv_hd_item_img3.setVisibility(View.VISIBLE);
					iv_hd_item_img3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 3);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img4 = map.get("imagepath4");
				if (img4 != null && !img4.equals("")) {
					imgurls.add(img4);
					imageLoader.displayImage(img4, iv_hd_item_img4, options,
							null);
					iv_hd_item_img4.setVisibility(View.VISIBLE);
					iv_hd_item_img4.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 4);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img5 = map.get("imagepath5");
				if (img5 != null && !img5.equals("")) {
					imgurls.add(img5);
					imageLoader.displayImage(img5, iv_hd_item_img5, options,
							null);
					iv_hd_item_img5.setVisibility(View.VISIBLE);
					iv_hd_item_img5.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 5);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img6 = map.get("imagepath6");
				if (img6 != null && !img6.equals("")) {
					imgurls.add(img6);
					imageLoader.displayImage(img6, iv_hd_item_img6, options,
							null);
					iv_hd_item_img6.setVisibility(View.VISIBLE);
					iv_hd_item_img6.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 6);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img7 = map.get("imagepath7");
				if (img7 != null && !img7.equals("")) {
					imgurls.add(img7);
					imageLoader.displayImage(img7, iv_hd_item_img7, options,
							null);
					iv_hd_item_img7.setVisibility(View.VISIBLE);
					iv_hd_item_img7.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 7);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img8 = map.get("imagepath8");
				if (img8 != null && !img8.equals("")) {
					imgurls.add(img8);
					imageLoader.displayImage(img8, iv_hd_item_img8, options,
							null);
					iv_hd_item_img8.setVisibility(View.VISIBLE);
					iv_hd_item_img8.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 8);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img9 = map.get("imagepath9");
				if (img9 != null && !img9.equals("")) {
					imgurls.add(img9);
					imageLoader.displayImage(img9, iv_hd_item_img9, options,
							null);
					iv_hd_item_img9.setVisibility(View.VISIBLE);
					iv_hd_item_img9.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 9);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img10 = map.get("imagepath10");
				if (img10 != null && !img10.equals("")) {
					imgurls.add(img10);
					imageLoader.displayImage(img10, iv_hd_item_img10, options,
							null);
					iv_hd_item_img10.setVisibility(View.VISIBLE);
					iv_hd_item_img10.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 10);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img11 = map.get("imagepath11");
				if (img11 != null && !img11.equals("")) {
					imgurls.add(img11);
					imageLoader.displayImage(img11, iv_hd_item_img11, options,
							null);
					iv_hd_item_img11.setVisibility(View.VISIBLE);
					iv_hd_item_img11.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 11);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img12 = map.get("imagepath12");
				if (img12 != null && !img12.equals("")) {
					imgurls.add(img12);
					imageLoader.displayImage(img12, iv_hd_item_img12, options,
							null);
					iv_hd_item_img12.setVisibility(View.VISIBLE);
					iv_hd_item_img12.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 12);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img13 = map.get("imagepath13");
				if (img13 != null && !img13.equals("")) {
					imgurls.add(img13);
					imageLoader.displayImage(img13, iv_hd_item_img13, options,
							null);
					iv_hd_item_img13.setVisibility(View.VISIBLE);
					iv_hd_item_img13.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 13);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img14 = map.get("imagepath14");
				if (img14 != null && !img14.equals("")) {
					imgurls.add(img14);
					imageLoader.displayImage(img14, iv_hd_item_img14, options,
							null);
					iv_hd_item_img14.setVisibility(View.VISIBLE);
					iv_hd_item_img14.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 14);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}
				final String img15 = map.get("imagepath15");
				if (img15 != null && !img15.equals("")) {
					imgurls.add(img15);
					imageLoader.displayImage(img15, iv_hd_item_img15, options,
							null);
					iv_hd_item_img15.setVisibility(View.VISIBLE);
					iv_hd_item_img15.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ActDetailActivity_disuse.this,
									ViewPagerByList.class);
							intent.putExtra("ID", 15);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter, 0);
						}
					});
				}

				hdvoteid = map.get("hdvoteid");
				if (hdvoteid != null && !hdvoteid.equals("")) {
					hdvote_title = map.get("hdvote_title");
					hdvote_votetype = map.get("hdvote_votetype");
					hdvote_startdate = map.get("hdvote_startdate");
					hdvote_enddate = map.get("hdvote_enddate");
					hdvote_maxcount = map.get("hdvote_maxcount");
					ll_hd_vote.setVisibility(View.VISIBLE);
					btn_hd_action.setVisibility(View.VISIBLE);
					btn_hd_action.setText("马上投票");
					action = "vote";
					tv_hd_votetitle.setText(hdvote_title);
				}

				hdsurveyid = map.get("hdsurveyid");
				if (hdsurveyid != null && !hdsurveyid.equals("")) {
					ll_hd_survey.setVisibility(View.VISIBLE);
					btn_hd_action.setVisibility(View.VISIBLE);
					btn_hd_action.setText("马上投票");
					action = "survey";
					tv_hd_surveytitle.setText(map.get("hdsurvey_title"));
				}

				pb_hd.setVisibility(View.GONE);

				// 正文线程执行完，再加载评论内容，投票内容
				initArrays(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						arrays = (List<PinglunData>) msg.obj;
						adapter = new ActDetailAdapter(ActDetailActivity_disuse.this,
								arrays, inflater, delmpl_url, reportypl_url,
								uid, densityDpi, id);
						listView.setAdapter(adapter);
						if (arrays != null) {
							// Collections.reverse(arrays);
							ll_hd_pinglun.setVisibility(View.VISIBLE);
							btn_pl_more.setVisibility(View.VISIBLE);
						} else {
							ll_hd_pinglun.setVisibility(View.VISIBLE);
							tv_hd_sofa.setVisibility(View.VISIBLE);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} else {
				pb_hd.setVisibility(View.GONE);
				// 加载失败更换“加载失败”底图
				// rl_hd_all.setBackgroundResource(R.drawable.bg_main);
				// Toast.makeText(getBaseContext(),
				// getResources().getString(R.string.content_fail),
				// Toast.LENGTH_SHORT).show();
				appMsg = AppMsg.makeText(ActDetailActivity_disuse.this, getResources()
						.getString(R.string.response_fail), style);
				appMsg.setLayoutGravity(Gravity.BOTTOM);
				appMsg.show();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<PinglunData> lst = new ArrayList<PinglunData>();

			@Override
			public void run() {
				try {
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	/**
	 * 从网络获取数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<PinglunData> getDataFromNetwork(int start, int end)
			throws Exception {
		List<PinglunData> lst = new ArrayList<PinglunData>();

		String resultString = HttpUtil.GetStringFromUrl(getpl_url + id
				+ "&startindex=" + start + "&endindex=" + end
				+ "&connected_uid=" + uid);
		if (resultString != null) {
			Type listType = new TypeToken<List<PinglunData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}

	public void playMusic(int action) {
		Intent intent = new Intent();
		intent.putExtra("MSG", action);
		intent.putExtra("url", voiceurl);
		intent.setClass(ActDetailActivity_disuse.this, ReplayPlayerService.class);
		startService(intent);
	}

	private void playorpause() {
		// //实现消息传递
		DelayThread delaythread = new DelayThread(500);
		delaythread.start();
		if (!flag) {
			if (voiceurl != null) {
				btn_mediaplay.setEnabled(false);
				btn_mediaplay.setVisibility(View.GONE);
				pb_voice_huanchong.setVisibility(View.VISIBLE);
				new Thread() {
					@Override
					public void run() {
						if (!playorpause) {
							try {
								Thread.sleep(200);
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
			}
		} else {
			playMusic(Constant.PAUSE);
			if (!playflag) {
				btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_play);
				playflag = true;
			} else {
				btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_pause);
				playflag = false;
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (ReplayPlayerService.mMediaPlayer != null) {
					int end = ReplayPlayerService.mMediaPlayer.getDuration();
					int begin = ReplayPlayerService.mMediaPlayer
							.getCurrentPosition();
					audioSeekBar.setMax(end);
					audioSeekBar.setProgress(begin);
					SimpleDateFormat sdf = null;
					String str_begin = null;
					String str_end = null;
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
					tv_voicetimerbegin.setText(str_begin);
					tv_voicetimer.setText(str_end);
				}
				break;
			case 200:
				showDialog((String) msg.obj);
				break;

			case 501:
				showDialog((String) msg.obj);
				break;

			case 502:
				showDialog((String) msg.obj);
				break;
			}
		}
	};

	/**
	 * 提示对话框
	 * 
	 * @param message
	 */
	private void showDialog(String message) {
		AlertDialog.Builder builer = new Builder(this);

		builer.setTitle("提示");
		builer.setMessage(message);
		builer.setCancelable(false);
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog dialog = builer.create();
		dialog.show();
	}

	// 在主线程里面处理消息并更新UI界面
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 3:
				pb_voice_huanchong.setVisibility(View.GONE);
				btn_mediaplay.setVisibility(View.VISIBLE);
				btn_mediaplay.setEnabled(true);
				btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_pause);
				break;
			}
		}
	};

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
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

	private void toVote() {
		// Intent intent = new
		// Intent(this,cn.fszt.trafficapp.wizardpager.MainActivity.class);
		Intent intent;
		if (hdvote_votetype != null && "0".equals(hdvote_votetype)) {
			intent = new Intent(this, VoteSingleChoiceActivity.class);
		} else {
			intent = new Intent(this, VoteMultipleChoiceActivity.class);
		}
		intent.putExtra("hdvote_title", hdvote_title);
		intent.putExtra("hdvote_votetype", hdvote_votetype);
		intent.putExtra("hdvote_startdate", hdvote_startdate);
		intent.putExtra("hdvote_enddate", hdvote_enddate);
		intent.putExtra("hdvoteid", hdvoteid);
		intent.putExtra("hdvote_maxcount", hdvote_maxcount);
		intent.putExtra("votetype", "vote");
		intent.putStringArrayListExtra("hdvoteoptions", hdvoteoptions);
		intent.putStringArrayListExtra("hdvoteoptionids", hdvoteoptionids);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_hd_action) {
			if ("vote".equals(action)) {
				toVote();
			} else if ("survey".equals(action)) {
				Intent intent = new Intent(ActDetailActivity_disuse.this,
						VoteSurveyListActivity.class);
				intent.putExtra("hdsurveyid", hdsurveyid);
				startActivity(intent);
			}
		}
		if (v == rl_hd_vote) {
			toVote();
		}
		if (v == btn_hd_vote) {
			toVote();
		}
		if (v == rl_hd_survey) {
			Intent intent = new Intent(ActDetailActivity_disuse.this,
					VoteSurveyListActivity.class);
			intent.putExtra("hdsurveyid", hdsurveyid);
			startActivity(intent);
		}
		if (v == btn_hd_survey) {
			Intent intent = new Intent(ActDetailActivity_disuse.this,
					VoteSurveyListActivity.class);
			intent.putExtra("hdsurveyid", hdsurveyid);
			startActivity(intent);
		}
		if (v == btn_mediaplay) {
			playorpause();
		}
		if (v == rl_videoview) {
			if (!video_flag) {
				pb_videoview.setVisibility(View.VISIBLE);
				mVideoView.setMediaController(mMediaController);
				mVideoView.setVideoURI(mUri);
				mVideoView.start();
				video_flag = true;
			}
		}
		if (v == btn_fullscreen) {
			Intent intent = new Intent(this, FullScreenVideoViewActivity.class);
			intent.putExtra("videourl", videourl);
			mPositionWhenPaused = mVideoView.getCurrentPosition();
			intent.putExtra("mPositionWhenPaused", mPositionWhenPaused);
			startActivityForResult(intent, FULLSCREEN);
		}
		if (v == btn_hd_baoming) {
			// 判断是否登录并已验证手机号
			String mobile = sp.getString("mobile", null);
			String validate = sp.getString("validate", null);
			if (uid == null) {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, LOGININ);
			} else if (uid != null && mobile == null
					|| validate.equals("false")) {
				// 已登录，但未验证手机号码
				AlertDialog.Builder builer = new Builder(this);

				builer.setTitle("提示");
				builer.setMessage("请先验证手机号码才能报名哦！");
				builer.setCancelable(false);
				builer.setPositiveButton("现在就去",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ActDetailActivity_disuse.this,
										SettingActivity.class);
								startActivityForResult(intent, VALIDATEMOBILE);
							}
						});

				builer.setNegativeButton("考虑一下", null);

				AlertDialog dialog = builer.create();
				dialog.show();

			} else if (uid != null && mobile != null && validate.equals("true")) {
				AlertDialog.Builder builer = new Builder(this);

				builer.setTitle("提示");
				builer.setMessage("亲爱的" + mobile + "用户，您已选择报名，请再次确认是否报名参加！");
				builer.setCancelable(false);
				builer.setPositiveButton("确定参加",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								new Thread() {
									JSONObject jsonObject;

									public void run() {
										String result = HttpUtil
												.GetStringFromUrl(hd_baoming_url
														+ "&connected_uid="
														+ uid
														+ "&hdinfoid="
														+ id);
										if (result != null
												&& !result.equals("")) {
											try {
												jsonObject = new JSONObject(
														result);
												String code = jsonObject
														.getString("code");
												String message = jsonObject
														.getString("message");
												if (code != null
														&& code.equals("200")) {
													// 报名成功
													handler.obtainMessage(200,
															message)
															.sendToTarget();
												} else if (code != null
														&& code.equals("501")) {
													// 重复报名
													handler.obtainMessage(501,
															message)
															.sendToTarget();
												} else if (code != null
														&& code.equals("502")) {
													// 报名时间不对
													handler.obtainMessage(502,
															message)
															.sendToTarget();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}
								}.start();
							}
						});

				builer.setNegativeButton("考虑一下", null);

				AlertDialog dialog = builer.create();
				dialog.show();
			}
		}

		if (v == btn_hd_tuangou) {
			// 判断是否登录并已验证手机号
			String mobile = sp.getString("mobile", null);
			String validate = sp.getString("validate", null);
			if (uid == null) {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, LOGININ);
			} else if (uid != null && mobile == null
					|| validate.equals("false")) {
				// 已登录，但未验证手机号码
				AlertDialog.Builder builer = new Builder(this);

				builer.setTitle("提示");
				builer.setMessage("请先验证手机号码才能报名哦！");
				builer.setCancelable(false);
				builer.setPositiveButton("现在就去",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ActDetailActivity_disuse.this,
										SettingActivity.class);
								startActivityForResult(intent, VALIDATEMOBILE);
							}
						});

				builer.setNegativeButton("考虑一下", null);

				AlertDialog dialog = builer.create();
				dialog.show();

			} else if (uid != null && mobile != null && validate.equals("true")) {
				AlertDialog.Builder builer = new Builder(this);

				builer.setTitle("提示");
				builer.setMessage("亲爱的" + mobile + "用户，您已选择报名团购，请再次确认是否参加团购！");
				builer.setCancelable(false);
				builer.setPositiveButton("确定参加",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								new Thread() {
									JSONObject jsonObject;

									public void run() {
										String result = HttpUtil
												.GetStringFromUrl(carlife_tuangou_url
														+ "&connected_uid="
														+ uid
														+ "&hdinfoid="
														+ id);
										if (result != null
												&& !result.equals("")) {
											try {
												jsonObject = new JSONObject(
														result);
												String code = jsonObject
														.getString("code");
												String message = jsonObject
														.getString("message");
												if (code != null
														&& code.equals("200")) {
													// 报名成功
													handler.obtainMessage(200,
															message)
															.sendToTarget();
												} else if (code != null
														&& code.equals("501")) {
													// 重复报名
													handler.obtainMessage(501,
															message)
															.sendToTarget();
												} else if (code != null
														&& code.equals("502")) {
													// 报名时间不对
													handler.obtainMessage(502,
															message)
															.sendToTarget();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}
								}.start();
							}
						});

				builer.setNegativeButton("考虑一下", null);

				AlertDialog dialog = builer.create();
				dialog.show();
			}
		}

		if (v == btn_pl_more) {
			Intent intent = new Intent(this, ActCommentListActivity.class);
			intent.putExtra("id", id);
			startActivityForResult(intent, PINGLUN);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == PINGLUN) {
			// 评论成功刷新评论列表
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					arrays = (List<PinglunData>) msg.obj;
					adapter = new ActDetailAdapter(ActDetailActivity_disuse.this, arrays,
							inflater, delmpl_url, reportypl_url, uid,
							densityDpi, id);
					listView.setAdapter(adapter);
					if (arrays != null) {
						// Collections.reverse(arrays);
						ll_hd_pinglun.setVisibility(View.VISIBLE);
						btn_pl_more.setVisibility(View.VISIBLE);
						tv_hd_sofa.setVisibility(View.GONE);
					} else {
						ll_hd_pinglun.setVisibility(View.VISIBLE);
						tv_hd_sofa.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
		if (resultCode == Activity.RESULT_OK && requestCode == FULLSCREEN) {
			mPositionWhenPaused = data.getIntExtra("mPosition", 0);
		}

		if (requestCode == LOGININ && resultCode == REQ_LOGIN) {
			uid = data.getStringExtra("uid");
			appMsg = AppMsg.makeText(this,
					getResources().getString(R.string.login_success), style);
			appMsg.setLayoutGravity(Gravity.BOTTOM);
			appMsg.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onPause() {
		mPositionWhenPaused = mVideoView.getCurrentPosition();
		mVideoView.stopPlayback();
		MobclickAgent.onPause(this);
		super.onPause();
	}

	public void onResume() {
		if (mPositionWhenPaused >= 0) {
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressflag = true;
		playorpause = true;
		Intent intent = new Intent();
		intent.setClass(ActDetailActivity_disuse.this, ReplayPlayerService.class);
		stopService(intent);// 停止Service
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(ActDetailActivity_disuse.this, ActListActivity.class);
			if (push != null && "true".equals(push)) {
				if ("huodong".equals(contenttype)) {
					intent.putExtra("contenttype", "huodong");
					intent.putExtra("push", "true");
					startActivity(intent);
					finish();
				} else if ("carlife".equals(contenttype)) {
					intent.putExtra("contenttype", "carlife");
					intent.putExtra("push", "true");
					startActivity(intent);
					finish();
				}
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
